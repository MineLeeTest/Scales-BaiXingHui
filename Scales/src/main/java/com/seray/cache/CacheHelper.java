package com.seray.cache;

import android.text.TextUtils;

import com.seray.message.LocalFileTag;
import com.seray.scales.R;
import com.seray.sjc.api.result.ActivateRsp;
import com.seray.sjc.db.AppDatabase;
import com.seray.sjc.db.dao.ConfigDao;
import com.seray.sjc.db.dao.PayTypeInfoDao;
import com.seray.sjc.entity.device.Config;
import com.seray.sjc.entity.device.PayTypeInfo;
import com.seray.sjc.entity.device.SjcParamInfo;
import com.seray.sjc.entity.device.TermInfo;
import com.seray.sjc.entity.order.OrderInfo;
import com.seray.sjc.entity.order.SjcDetail;
import com.seray.util.FileHelp;
import com.seray.util.LogUtil;
import com.seray.util.NumFormatUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CacheHelper {

    /*四个客户的销售明细缓存*/
    public static List<SjcDetail> Detail_1 = new ArrayList<>();
    public static List<SjcDetail> Detail_2 = new ArrayList<>();
    public static List<SjcDetail> Detail_3 = new ArrayList<>();
    public static List<SjcDetail> Detail_4 = new ArrayList<>();

    /**
     * 上笔交易明细
     */
    public static SjcDetail latestDetail = null;

    /**
     * 上笔交易明细是否已经插入数据库
     */
    public static boolean isSaved = false;

    /**
     * 支持的支付类型
     */
    public static List<PayTypeInfo> PayTypeInfoList = new ArrayList<>();

    /**
     * 流水号排序号
     */
    public static int DATE_ID;

    /**
     * 缓存交易信息
     * 注意：目前只保留最近一笔 未做多笔订单缓存
     */
    public static List<OrderInfo> orderCache = new ArrayList<>();

    /**
     * 累计客户按钮初始化文字
     */
    public static int[] basicBtnText = {R.string.fn_1, R.string.fn_2, R.string.fn_3, R.string.fn_4};

    /*设备信息 用于激活验证*/
    public static String TermId;// 终端ID
    public static String TermCode;// 终端编码
    public static String TermName;// 终端名称
    public static String DeviceCode;// 设备号（工厂下发）

    /*商户信息 用户打印*/
    public static String ShopName;// 商铺(门店)名称
    public static String BoothId;// 摊位编码
    public static String BoothName;// 摊位名称
    public static String MarketName;// 市场(公司,商场)名称

    /*配置信息 SjcParamInfo转换Config*/
    public static String ResourceBaseUrl;// 后显示等多媒体资源基础地址
    public static String TracingBaseUr;// 二维码溯源基础地址
    public static long HeartBeatTime = 1800;// 心跳包发送时间间隔
    public static long SyncDownTime = 1800;// 业务数据同步时间间隔

    // 果蔬识别最小阈值
    public static int MinRecognizeValue = 20;

    /*开关类设置*/
    public static boolean isOpenForceRecord;
    public static boolean isOpenCamera;
    public static boolean isNoPluNoSum;
    public static boolean isChangePrice;
    public static boolean isHoldPlu;
    public static boolean isPrintPrice;
    public static boolean isPrintRecognizeLog;
    public static boolean isOpenBackDisplay;
    public static boolean isHoldPrice;
    public static boolean isOpenJin;
    public static boolean isOpenPrintQR;
    public static boolean isOpenBattery;
    public static boolean isOpenCollection;

    public static void cleanLocalCache() {
        if (Detail_1 != null) {
            Detail_1.clear();
            Detail_1 = null;
        }
        if (Detail_2 != null) {
            Detail_2.clear();
            Detail_2 = null;
        }
        if (Detail_3 != null) {
            Detail_3.clear();
            Detail_3 = null;
        }
        if (Detail_4 != null) {
            Detail_4.clear();
            Detail_4 = null;
        }
        if (orderCache != null) {
            orderCache.clear();
            orderCache = null;
        }
        if (PayTypeInfoList != null) {
            PayTypeInfoList.clear();
            PayTypeInfoList = null;
        }
        latestDetail = null;
        basicBtnText = null;
    }

    /**
     * 缓存订单详情
     */
    public static LocalFileTag addOrderToCache(OrderInfo info) {
        orderCache.clear();// 清空缓存数据
        orderCache.add(info);// 插入
        return FileHelp.writeToReprint(info);// 写入备份文件
    }

    /**
     * 从缓存中读取订单
     */
    public static OrderInfo getOrderInfoFromCache() {
        if (!orderCache.isEmpty()) {
            return orderCache.get(0);
        }
        return null;
    }

    /**
     * 统一部署
     */
    public static void prepareCacheData() {
        prepareLastOrderInfo();
        getDeviceCode();
        preparePayTypeInfoList();
        Map<String, String> map = getConfigMap();
        prepareOrderSortNumber(map);
        prepareTermConfig(map);
        prepareParamInfo(map);
        prepareConfig(map);
    }

    /**
     * 部署打印信息
     */
    public static void prepareTermConfig() {
        Map<String, String> configMap = getConfigMap();
        prepareTermConfig(configMap);
    }

    /**
     * 部署设置信息
     */
    public static void prepareConfig() {
        Map<String, String> configMap = getConfigMap();
        prepareConfig(configMap);
    }

    /**
     * 部署参数信息
     */
    public static void prepareParamInfo() {
        Map<String, String> configMap = getConfigMap();
        prepareParamInfo(configMap);
    }

    private static void getDeviceCode() {
        // 读取出厂下发的设备编码
        LocalFileTag tag = FileHelp.readDeviceCode();
        Object obj = tag.getObj();
        if (tag.isSuccess() && obj != null) {
            DeviceCode = (String) tag.getObj();
            LogUtil.e("设备编码：" + DeviceCode);
        }
    }

    /**
     * 部署缓存交易订单
     */
    private static void prepareLastOrderInfo() {
        LocalFileTag tag = FileHelp.readReprintContent();
        Object obj = tag.getObj();
        OrderInfo info;
        if (tag.isSuccess() && obj != null) {
            if (obj instanceof OrderInfo) {
                info = (OrderInfo) obj;
                orderCache.clear();
                orderCache.add(info);
            }
        }
    }

    /**
     * 部署支付类型
     */
    public static void preparePayTypeInfoList() {
        PayTypeInfoList.clear();
        PayTypeInfoDao dao = AppDatabase.getInstance().getPayTypeDao();
        List<PayTypeInfo> payTypeInfos = dao.loadAll();
        if (payTypeInfos != null) {
            PayTypeInfoList.addAll(payTypeInfos);
        }
        // 添加现金支付
        PayTypeInfoList.add(new PayTypeInfo("0101", "现金支付"));
    }

    /**
     * 部署参数信息
     * 统一部署
     */
    private static void prepareParamInfo(Map<String, String> map) {
        String resourceUrl = map.get(SjcParamInfo.RESOURCE_URL);
        String tracingUrl = map.get(SjcParamInfo.TRACING_URL);
        String heartBeatTime = map.get(SjcParamInfo.HEART_BEAT_TIME);
        String syncDownTime = map.get(SjcParamInfo.SYNC_DOWN_TIME);
        String minRecognizeValue = map.get(SjcParamInfo.MinRecognizeValue);

        ResourceBaseUrl = isAccord(resourceUrl) ? "" : resourceUrl;// 多媒体资源下载基础地址

        TracingBaseUr = isAccord(tracingUrl) ? "" : tracingUrl;// 溯源基础地址

        boolean heartAccord = isAccord(heartBeatTime);
        if (!heartAccord) {
            if (NumFormatUtil.isInt(heartBeatTime)) {
                HeartBeatTime = Long.parseLong(heartBeatTime);// 心跳包发送间隔
            }
        }

        boolean syncAccord = isAccord(syncDownTime);
        if (!syncAccord) {
            if (NumFormatUtil.isInt(syncDownTime)) {
                SyncDownTime = Long.parseLong(syncDownTime);// 业务数据自动更新间隔
            }
        }

        boolean minRecognize = isAccord(minRecognizeValue);
        if (!minRecognize) {
            if (NumFormatUtil.isInt(minRecognizeValue)) {
                MinRecognizeValue = Integer.parseInt(minRecognizeValue);// 果蔬识别最小阈值
            }
        }
    }

    /**
     * 部署交易流水号排序号
     * 用于统一部署
     */
    private static void prepareOrderSortNumber(Map<String, String> map) {
        String data_id = map.get("Date_Id");
        DATE_ID = isAccord(data_id) ? 0 : Integer.parseInt(data_id);
    }

    /**
     * 部署打印信息
     * 用于统一部署
     */
    private static void prepareTermConfig(Map<String, String> map) {
        String termId = map.get(ActivateRsp.TERM_ID);
        String termCode = map.get(ActivateRsp.TERM_CODE);
        String termName = map.get(TermInfo.TERM_NAME);
        String shopName = map.get(TermInfo.SHOP_NAME);
        String boothName = map.get(TermInfo.BOOTH_NAME);
        String boothId = map.get(TermInfo.BOOTH_CODE);
        String marketName = map.get(TermInfo.MARKET_NAME);

        TermId = isAccord(termId) ? "" : termId;// 终端ID 服务器给定

        TermCode = isAccord(termCode) ? "" : termCode;// 终端编码 服务器给定

        TermName = isAccord(termName) ? "" : termName;// 终端名称 服务器给定

        ShopName = isAccord(shopName) ? "" : shopName;// 商铺(门店)名称 服务器给定

        BoothName = isAccord(boothName) ? "" : boothName;// 摊位名称

        BoothId = isAccord(boothId) ? "" : boothId;// 摊位编码

        MarketName = isAccord(marketName) ? "" : marketName;// 市场(公司,商场)名称

    }

    /**
     * 部署设置信息
     * 用于统一部署
     */
    private static void prepareConfig(Map<String, String> map) {
        String forceRecord = map.get("isOpenForceRecord");
        String camera = map.get("isOpenCamera");
        String noPluNoSum = map.get("isNoPluNoSum");
        String changePrice = map.get("isChangePrice");
        String holdPlu = map.get("isHoldPlu");
        String printPrice = map.get("isPrintPrice");
        String printRecognizeLog = map.get("isPrintRecognizeLog");
        String openDisplay = map.get("isOpenBackDisplay");
        String holdPrice = map.get("isHoldPrice");
        String openJin = map.get("isOpenJin");
        String openPrintQR = map.get("isOpenPrintQR");
        String openBattery = map.get("isOpenBattery");
        String openCollection = map.get("isOpenCollection");

        isOpenForceRecord = forceRecord != null && forceRecord.equals("1");// 是否开启强制记录功能//默认关闭

        isOpenCamera = camera != null && camera.equals("1"); // 是否开启照相功能//默认关闭

        isNoPluNoSum = noPluNoSum == null || noPluNoSum.equals("1"); // 是否开启无PLU不显示总价//默认开启

        isHoldPlu = holdPlu == null || holdPlu.equals("1");// 是否开启交易完成保留PLU//默认开启

        isHoldPrice = holdPrice == null || holdPrice.equals("1");// 是否开启交易完成保留单价/默认开启

        isPrintPrice = printPrice == null || printPrice.equals("1");// 是否开启打印单价/默认开启

        isPrintRecognizeLog = printRecognizeLog != null && printRecognizeLog.equals("1");// 是否开启打印识别日志/默认关闭

        isOpenBackDisplay = openDisplay == null || openDisplay.equals("1");// 是否开启后显示//默认开启

        isOpenJin = openJin != null && openJin.equals("1");// 是否开启了称重单位(斤)//默认关闭

        isChangePrice = changePrice == null || changePrice.equals("1");//是否允许修改单价//默认开启

        isOpenPrintQR = openPrintQR == null || openPrintQR.equals("1");// 是否开启打印二维码//默认开启

        isOpenBattery = openBattery != null && openBattery.equals("1");// 是否开启电量显示//默认关闭

        isOpenCollection = openCollection == null || openCollection.equals("1");// 是否开启后显示收款码//默认开启
    }

    /**
     * 保存订单流水号后三位
     */
    public static void saveDataIdToDb() {
        Config config = new Config();
        config.setConfigKey("Date_Id");
        config.setConfigValue(String.format(Locale.CHINA, "%03d", DATE_ID));
        AppDatabase.getInstance().getConfigDao().save(config);
    }

    /**
     * 获取所有已保存的配置信息
     *
     * @return 已保存的配置信息
     */
    private static Map<String, String> getConfigMap() {
        Map<String, String> data = new HashMap<>();
        ConfigDao configDao = AppDatabase.getInstance().getConfigDao();
        List<Config> configs = configDao.loadAll();
        for (Config bean : configs) {
            String configKey = bean.getConfigKey();
            String configValue = bean.getConfigValue();
            data.put(configKey, configValue);
        }
        return data;
    }

    /**
     * 是否满足插入或更新数据库条件
     */
    private static boolean isAccord(String value) {
        return TextUtils.isEmpty(value);
    }
}
