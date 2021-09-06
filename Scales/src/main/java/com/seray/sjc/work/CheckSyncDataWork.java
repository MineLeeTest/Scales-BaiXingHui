//package com.seray.sjc.work;
//
//import android.content.Context;
//import android.support.annotation.NonNull;
//
//import com.seray.cache.CacheHelper;
//import com.seray.message.LocalFileTag;
//import com.seray.sjc.poster.SjcUpdatePoster;
//import com.seray.sjc.api.net.HttpServicesFactory;
//import com.seray.sjc.api.request.BusinessReq;
//import com.seray.sjc.api.result.ApiDataRsp;
//import com.seray.sjc.api.result.BusinessRsp;
//import com.seray.sjc.db.AppDatabase;
//import com.seray.sjc.db.dao.ConfigDao;
//import com.seray.sjc.db.dao.PayTypeInfoDao;
//import com.seray.sjc.db.dao.ScreenResourceDao;
//import com.seray.sjc.db.dao.SjcCategoryDao;
//import com.seray.sjc.db.dao.SjcProductDao;
//import com.seray.sjc.entity.device.ConfigADB;
//import com.seray.sjc.entity.device.PayTypeInfo;
//import com.seray.sjc.entity.device.ScreenResource;
//import com.seray.sjc.entity.device.SjcParamInfo;
//import com.seray.sjc.entity.device.TermInfo;
//import com.seray.sjc.entity.message.SyncDataWorkMessage;
//import com.seray.sjc.entity.product.SjcCategory;
//import com.seray.sjc.entity.product.SjcProduct;
//import com.seray.util.DateUtil;
//import com.seray.util.FileHelp;
//import com.seray.util.LogUtil;
//
//import org.greenrobot.eventbus.EventBus;
//
//import java.util.Date;
//import java.util.List;
//import java.util.Map;
//
//import androidx.work.Data;
//import androidx.work.Worker;
//import androidx.work.WorkerParameters;
//import retrofit2.Response;
//
///**
// * 周期性更新数据任务
// */
//public class CheckSyncDataWork extends Worker {
//
//    public static final String KEY_FORCE_UPDATE = "ForceUpdateKey";
//
//    private AppDatabase mDatabase;
//
//    public CheckSyncDataWork(@NonNull Context context, @NonNull WorkerParameters workerParams) {
//        super(context, workerParams);
//        this.mDatabase = AppDatabase.getInstance();
//    }
//
//    @NonNull
//    @Override
//    public Result doWork() {
//
//        Data inputData = getInputData();
//        boolean forceUpdate = inputData.getBoolean("", false);
//
//        BusinessReq businessReq = getBusinessReq();
//
//        if (forceUpdate || businessReq == null) {
//            businessReq = new BusinessReq();
//            businessReq.lastSynDate = DateUtil.getDateStr(new Date(), "");
//            businessReq.termId = CacheHelper.device_id;
//            businessReq.lastSynResult = "0";
//        }
//
//        try {
//
//            LLog.i("检查周期性更新数据的任务");
//
//            Response<ApiDataRsp<BusinessRsp>> response = HttpServicesFactory.getHttpServiceApi()
//                    .getBusinessData(businessReq)
//                    .execute();
//
//            if (response.isSuccessful()) {
//                //存储数据
//                LLog.i("检查周期性更新数据的任务 成功");
//                ApiDataRsp<BusinessRsp> body = response.body();
//                if (body != null && body.success) {
//                    BusinessRsp resp = body.msgs;
//                    try {
//                        mDatabase.beginTransaction();
//                        saveCategories(resp.goodsCategory, resp.goodsCategoryList);
//                        saveProducts(resp.goodsInfo, resp.goodsInfoList);
//                        saveParamInfoList(resp.paramInfo, resp.paramInfoList);
//                        savePayTypes(resp.payTypeInfo, resp.payTypeList);
//                        saveTermInfo(resp.termInfo, resp.termInfoObj);
//                        saveScreenSources(resp.screenResource, resp.screenResourceList);
//                        businessReq.lastSynDate = DateUtil.getDateStr(new Date(), "");
//                        businessReq.lastSynResult = "1";
//                        setBusinessReq(businessReq);
//                        mDatabase.setTransactionSuccessful();
//                        LLog.i("检查周期性更新数据的任务 存储数据 成功");
//                    } catch (Exception e) {
//                        businessReq.lastSynDate = DateUtil.getDateStr(new Date(), "");
//                        businessReq.lastSynResult = "2";
//                        setBusinessReq(businessReq);
//                        LogUtil.e("检查周期性更新数据的任务 存储数据 失败");
//                    } finally {
//                        mDatabase.endTransaction();
//                    }
//                    EventBus.getDefault().post(new SyncDataWorkMessage(true));
//                    return Result.success();
//                }
//            }
//        } catch (Exception e) {
//            LLog.i("检查周期性更新数据的任务 失败" + e.getMessage());
//        }
//        EventBus.getDefault().post(new SyncDataWorkMessage(false));
//        return Result.failure();
//    }
//
//    private BusinessReq getBusinessReq() {
//        LocalFileTag tag = FileHelp.readBussiReq();
//        if (tag.success()) {
//            return (BusinessReq) tag.getObj();
//        }
//        return null;
//    }
//
//    /**
//     * 存储本次请求状态
//     */
//    private void setBusinessReq(BusinessReq businessReq) {
//        FileHelp.writeToBussiReq(businessReq);
//    }
//
//    private boolean isDoSave(String serverStatus) {
//        if (serverStatus == null || serverStatus.isEmpty()) {
//            return true;
//        }
//        // 未通讯
//        return serverStatus.equals("1");
//    }
//
//    private void saveCategories(String status, List<SjcCategory> data) {
//        boolean doSave = isDoSave(status);
//        if (doSave && data != null) {
//            SjcCategoryDao categoryDao = mDatabase.getCategoryDao();
//            categoryDao.delete();
//            categoryDao.save(data);
//        }
//    }
//
//    private void saveProducts(String status, List<SjcProduct> data) {
//        boolean doSave = isDoSave(status);
//        if (doSave && data != null) {
//            SjcProductDao productDao = mDatabase.getProductDao();
//            productDao.delete();
//            productDao.save(data);
//            SjcUpdatePoster.notifyProductReload();
//        }
//    }
//
//    private void saveScreenSources(String status, List<ScreenResource> data) {
//        boolean doSave = isDoSave(status);
//        if (doSave && data != null) {
//            ScreenResourceDao screenResourceDao = mDatabase.getScreenResourceDao();
//            screenResourceDao.delete();
//            screenResourceDao.save(data);
//            SjcUpdatePoster.notifyScreenResourceReload();
//        }
//    }
//
//    private void savePayTypes(String status, List<PayTypeInfo> data) {
//        boolean doSave = isDoSave(status);
//        if (doSave && data != null) {
//            PayTypeInfoDao payTypeDao = mDatabase.getPayTypeDao();
//            payTypeDao.delete();
//            payTypeDao.save(data);
//            SjcUpdatePoster.notifyPayTypeReload();
//        }
//    }
//
//    private void saveParamInfoList(String status, List<SjcParamInfo> data) {
//        boolean doSave = isDoSave(status);
//        if (doSave && data != null) {
//            ConfigDao configDao = AppDatabase.getInstance().getConfigDao();
//            for (SjcParamInfo bean : data) {
//                String paramKey = bean.getParamKey();
//                String paramValue = bean.getParamValue();
//                if (paramKey == null || paramKey.isEmpty()) {
//                    continue;
//                }
//                ConfigADB query = new ConfigADB(paramKey, paramValue);
//                configDao.save(query);
//            }
//            SjcUpdatePoster.notifyParamInfoReload();
//        }
//    }
//
//    private void saveTermInfo(String status, TermInfo msgs) {
//        boolean doSave = isDoSave(status);
//        if (doSave && msgs != null) {
//            ConfigDao configDao = AppDatabase.getInstance().getConfigDao();
//            Map<String, String> map = msgs.toMap();
//            for (Map.Entry<String, String> entry : map.entrySet()) {
//                String key = entry.getKey();
//                String value = entry.getValue();
//                if (key == null || key.isEmpty()) {
//                    continue;
//                }
//                ConfigADB query = new ConfigADB(key, value);
//                configDao.save(query);
//            }
//            SjcUpdatePoster.notifyDeviceInfoReload();
//        }
//    }
//}
