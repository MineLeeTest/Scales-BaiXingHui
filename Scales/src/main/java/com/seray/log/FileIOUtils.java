package com.seray.log;

import android.content.Context;
import android.os.Environment;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Author：李程
 * CreateTime：2019/4/19 13:35
 * E-mail：licheng@kedacom.com
 * Describe：
 */
public class FileIOUtils {

    private static final String LOGS_DIR_NAME = "logs";
    private static final String DB_DIR_NAME = "database";
    private static final String CACHE_DIR_NAME = "cahce";
    public static String DB_PATH;
    public static String LOCAL_PATH;

    private FileIOUtils() {
    }

    public static String getSDCardPathByEnvironment() {
        return "mounted".equals(Environment.getExternalStorageState()) ? Environment.getExternalStorageDirectory().getAbsolutePath() : "";
    }

    public static void init(Context context) {
        LOCAL_PATH = getSDCardPathByEnvironment() + File.separator + context.getPackageName() + File.separator;
        DB_PATH = LOCAL_PATH + DB_DIR_NAME + File.separator;
    }

    public static String getSystemLogDirectoryPath() {
        return LOCAL_PATH + LOGS_DIR_NAME + File.separator;
    }

    public static String getCacheDirectoryPath() {
        return LOCAL_PATH + CACHE_DIR_NAME + File.separator;
    }

    public static void writeFileFromBytesByStream(final File file,
                                                  final byte[] bytes,
                                                  final boolean append) {
        if (bytes == null || !createOrExistsFile(file)) return;
        BufferedOutputStream bos = null;
        try {
            bos = new BufferedOutputStream(new FileOutputStream(file, append));
            bos.write(bytes);
            bos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bos != null) {
                    bos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean createOrExistsFile(final File file) {
        if (file == null) return false;
        if (file.exists()) return file.isFile();
        if (!createOrExistsDir(file.getParentFile())) return false;
        try {
            return file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean createOrExistsDir(final File file) {
        return file != null && (file.exists() ? file.isDirectory() : file.mkdirs());
    }

    public static void writeFileFromString(final File file, final String content, final boolean append) {
        if (file == null || content == null) return;
        if (!createOrExistsFile(file)) return;
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(file, append));
            bw.write(content);
            bw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static File getFileByPath(final String filePath) {
        return isSpace(filePath) ? null : new File(filePath);
    }

    private static boolean isSpace(final String s) {
        if (s == null) return true;
        for (int i = 0, len = s.length(); i < len; ++i) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}