package com.seray.util;

import android.os.Environment;
import android.util.Base64;

import com.seray.message.LocalFileTag;
import com.seray.sjc.entity.device.ProductADB;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * 文本读写辅助类
 */
public class FileHelp {

    /**
     * 用于储存二维码支付图片
     */
    public static final String PAY_CODE_PIC_DIR = Environment.getExternalStorageDirectory() +
            "/infol/payCode/";

    /**
     * 本应用的外部数据库
     */
    public static final String DATABASE_DIR = Environment.getExternalStorageDirectory() +
            "/infol/database/";

    /**
     * 本地屏幕校准文件
     */
    private static final String CALIBRATION_PATH = "/data/system/calibration";

    private static final String CACHE_DIR_PATH = Environment.getExternalStorageDirectory() + "/infol/cache/";

    /**
     * 本地设备基本信息（设备号）
     * 固定不能修改
     */
    private static final String DEVICE_PATH = Environment.getExternalStorageDirectory() + "/InfolFactory/device_code.txt";

    /**
     * 用于记录用户选择展示商品路径
     */
    private static final String SEL_PRODUCT_PATH = CACHE_DIR_PATH + "products.txt";

    /**
     * 用于重印上笔交易路径
     */
    private static final String REPRINT_PATH = CACHE_DIR_PATH + "reprint.txt";

    /**
     * 用于保存基础数据同步的文本路径
     */
    private static final String BUSSIREQ_PATH = CACHE_DIR_PATH + "bussireq.txt";

    /**
     * 用于保存订单临时信息路径
     */
    public static final String ORDER_INFO = CACHE_DIR_PATH + "cacheOrder.txt";

    /**
     * 删除本地屏幕校准文件
     */
    public static boolean deleteCalibrationFile() {
        File file = new File(CALIBRATION_PATH);
        return file.exists() && file.delete();
    }

    /**
     * base64解码成图片
     */
    public static boolean generateImage(String imgStr) {
        //图像数据为空
        if (imgStr == null) {
            return false;
        }
        File dirFile = new File(FileHelp.PAY_CODE_PIC_DIR);

        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }

        byte[] bytes = Base64.decode(imgStr.getBytes(), Base64.DEFAULT);

        try {
            for (int i = 0; i < bytes.length; ++i) {
                if (bytes[i] < 0) {//调整异常数据
                    bytes[i] += 256;
                }
            }
            //生成jpeg图片
            OutputStream out = new FileOutputStream(PAY_CODE_PIC_DIR + "payCode.jpg");
            out.write(bytes);
            out.flush();
            out.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static LocalFileTag prepareConfigDir() {
        File file = new File("/data/seray/config");
        LocalFileTag tag = new LocalFileTag();
        if (!file.exists()) {
            if (!file.mkdirs()) {
                tag.setSuccess(false);
                tag.setContent(file.getAbsolutePath() + " create failed");
            }
        } else {
            tag.setSuccess(true);
        }
        return tag;
    }

    /**
     * 读取设备编码（工厂）
     */
    public static LocalFileTag readDeviceCode() {
        return readContent(new File(DEVICE_PATH));
    }

    /**
     * 读取文件内容
     */
    private static LocalFileTag readContent(File file) {
        LocalFileTag tag = new LocalFileTag();
        String content;
        FileInputStream is = null;
        tag.setSuccess(false);
        if (file.exists()) {
            try {
                is = new FileInputStream(file);
                int offset = 0, len;
                byte[] bs = new byte[1024 * 8];
                while ((len = is.read(bs, offset, bs.length - offset)) != -1) {
                    offset += len;
                    if (offset >= bs.length) {
                        byte[] temp = new byte[bs.length * 2];
                        System.arraycopy(bs, 0, temp, 0, offset);
                        bs = temp;
                    }
                }
                content = new String(bs, 0, offset);
                tag.setObj(content);
                tag.setSuccess(true);
                LogUtil.d("read " + file.getAbsolutePath() + "[" + content + "] success");
            } catch (Exception e) {
                tag.setSuccess(false);
                tag.setContent(e.getMessage());
                LogUtil.e("read " + file.getAbsolutePath() + " " + e.getMessage());
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        tag.setSuccess(false);
                        tag.setContent(e.getMessage());
                        LogUtil.e(e.getMessage());
                    }
                }
            }
        }
        return tag;
    }

    /**
     * 写入文本内容
     */
    private static LocalFileTag writeContent(File file, String content) {
        FileOutputStream os = null;
        LocalFileTag tag = new LocalFileTag();
        try {
            os = new FileOutputStream(file);
            os.write(content.getBytes());
            os.flush();
            tag.setSuccess(true);
            LogUtil.d("write [" + content + "] to " + file.getAbsolutePath() + " success");
        } catch (Exception e) {
            tag.setContent(e.getMessage());
            LogUtil.e(e.getMessage());
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    tag.setSuccess(false);
                    tag.setContent(e.getMessage());
                    LogUtil.e(e.getMessage());
                }
            }
        }
        return tag;
    }

    /**
     * 创建文件及父文件夹
     */
    private static File createNewFile(String fileName) {
        File file = new File(fileName);
        File parFile = file.getParentFile();
        if (!parFile.exists()) {
            if (parFile.mkdirs())
                LogUtil.d("create new dir file " + parFile.getAbsolutePath());
        }
        if (!file.exists()) {
            try {
                if (file.createNewFile())
                    LogUtil.d("create new file " + file.getAbsolutePath());
            } catch (IOException e) {
                LogUtil.e(e.getMessage());
            }
        }
        return file;
    }

    /**
     * 用于判断是否是刚烧录的电子秤
     */
    public static boolean isSerayDirExists() {
        File file = new File(REPRINT_PATH);
        File dirFile = file.getParentFile();
        return dirFile.exists();
    }

    /**
     * 写入重印备份文件
     */
    public static LocalFileTag writeToReprint(Object info) {
        return writeObject(createNewFile(REPRINT_PATH), info);
    }

    /**
     * 写对象至指定文件
     */
    private static LocalFileTag writeObject(File file, Object obj) {
        ObjectOutputStream oos = null;
        LocalFileTag tag = new LocalFileTag();
        tag.setSuccess(false);
        try {
            oos = new ObjectOutputStream(new FileOutputStream(file));
            oos.writeObject(obj);
            oos.flush();
            tag.setSuccess(true);
            LogUtil.d(obj == null ? "null" : obj.toString() + " write to " + file.getAbsolutePath
                    () + " success");
        } catch (IOException e) {
            tag.setContent(e.getMessage());
        } finally {
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException e) {
                    tag.setSuccess(false);
                    tag.setContent(e.getMessage());
                }
            }
        }
        return tag;
    }

    /**
     * 读取重印备份文件内容
     */
    public static LocalFileTag readReprintContent() {
        return readObject(new File(REPRINT_PATH));
    }

    /**
     * 读取对象文件
     */
    private static LocalFileTag readObject(File file) {
        ObjectInputStream ois = null;
        Object obj;
        LocalFileTag tag = new LocalFileTag();
        tag.setSuccess(false);
        if (file.exists()) {
            try {
                ois = new ObjectInputStream(new FileInputStream(file));
                obj = ois.readObject();
                tag.setObj(obj);
                tag.setSuccess(true);
                LogUtil.d("read Object[" + obj.toString() + "] from " + file.getAbsolutePath() + " success");
            } catch (Exception e) {
                tag.setContent(e.getMessage());
                LogUtil.e("read " + file.getAbsolutePath() + " error " + e.getMessage());
            } finally {
                if (ois != null) {
                    try {
                        ois.close();
                    } catch (IOException e) {
                        tag.setContent(e.getMessage());
                        LogUtil.e(e.getMessage());
                    }
                }
            }
        }
        return tag;
    }

    /**
     * 写入用户选择的商品信息
     */
    public static LocalFileTag writeToProducts(List<ProductADB> list) {
        LocalFileTag tag = null;
        if (!list.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < list.size(); i++) {
                sb.append(list.get(i).getPro_name()).append(";");
            }
            String content = sb.toString();
            content = content.substring(0, content.length() - 1);
            tag = writeContent(createNewFile(SEL_PRODUCT_PATH), content);
        }
        return tag;
    }

    /**
     * 读取用户选择的商品信息
     */
    public static LocalFileTag readSelectedProducts() {
        return readContent(new File(SEL_PRODUCT_PATH));
    }

    /**
     * 写入基础信息同步请求参数
     */
    public static LocalFileTag writeToBussiReq(Object list) {
        return writeObject(createNewFile(BUSSIREQ_PATH), list);
    }

    /**
     * 读取基础信息同步的请求参数
     */
    public static LocalFileTag readBussiReq() {
        return readObject(new File(BUSSIREQ_PATH));
    }

//    /**
//     * 写入临时订单信息
//     */
//    public static LocalFileTag writeToOrderInfo(OrderInfo orderInfo) {
//        return writeObject(createNewFile(ORDER_INFO), orderInfo);
//    }

    public static boolean deleteCache() {
        File file = new File(CACHE_DIR_PATH);
        return deleteDir(file);
    }

    public static boolean deleteDir(final File dir) {
        if (dir == null) return false;
        if (!dir.exists()) return true;
        if (!dir.isDirectory()) return false;
        File[] files = dir.listFiles();
        if (files != null && files.length != 0) {
            for (File file : files) {
                if (file.isFile()) {
                    if (!file.delete()) return false;
                } else if (file.isDirectory()) {
                    if (!deleteDir(file)) return false;
                }
            }
        }
        return dir.delete();
    }
}
