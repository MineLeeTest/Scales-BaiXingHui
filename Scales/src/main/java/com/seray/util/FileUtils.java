package com.seray.util;

import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FileUtils {
    public static final String FILE_SUFFIX_SEPARATOR = ".";

    public FileUtils() {
    }

    public static StringBuilder readFile2String(String filePath, String charsetName) {
        File file = new File(filePath);
        StringBuilder fileContent = new StringBuilder("");
        if (file != null && file.isFile()) {
            BufferedReader reader = null;

            try {
                InputStreamReader is = new InputStreamReader(new FileInputStream(file), charsetName);
                reader = new BufferedReader(is);

                for(String line = null; (line = reader.readLine()) != null; fileContent.append(line)) {
                    if (!fileContent.toString().equals("")) {
                        fileContent.append("\r\n");
                    }
                }

                StringBuilder var7 = fileContent;
                return var7;
            } catch (IOException var11) {
                throw new RuntimeException("IOException", var11);
            } finally {
                close(reader);
            }
        } else {
            return null;
        }
    }

    public static boolean writeFile(String filePath, String content, boolean append) {
        if (TextUtils.isEmpty(content)) {
            return false;
        } else {
            FileWriter fileWriter = null;

            boolean var4;
            try {
                makeDirs(filePath);
                fileWriter = new FileWriter(filePath, append);
                fileWriter.write(content);
                var4 = true;
            } catch (IOException var8) {
                throw new RuntimeException("IOException occurred. ", var8);
            } finally {
                close(fileWriter);
            }

            return var4;
        }
    }

    public static boolean writeFile(String filePath, String content) {
        return writeFile(filePath, content, false);
    }

    public static boolean writeFile(String filePath, InputStream is) {
        return writeFile(filePath, is, false);
    }

    public static boolean writeFile(String filePath, InputStream is, boolean append) {
        return writeFile(filePath != null ? new File(filePath) : null, is, append);
    }

    public static boolean writeFile(File file, InputStream is) {
        return writeFile(file, is, false);
    }

    public static boolean writeFile(File file, InputStream is, boolean append) {
        FileOutputStream o = null;

        try {
            makeDirs(file.getAbsolutePath());
            o = new FileOutputStream(file, append);
            byte[] data = new byte[1024];
            boolean var5 = true;

            int length;
            while((length = is.read(data)) != -1) {
                o.write(data, 0, length);
            }

            o.flush();
            boolean var6 = true;
            return var6;
        } catch (FileNotFoundException var11) {
            throw new RuntimeException("FileNotFoundException", var11);
        } catch (IOException var12) {
            throw new RuntimeException("IOException", var12);
        } finally {
            close(o);
            close(is);
        }
    }

    public static void moveFile(String srcFilePath, String destFilePath) throws FileNotFoundException {
        if (!TextUtils.isEmpty(srcFilePath) && !TextUtils.isEmpty(destFilePath)) {
            moveFile(new File(srcFilePath), new File(destFilePath));
        } else {
            throw new RuntimeException("Both srcFilePath and destFilePath cannot be null.");
        }
    }

    public static void moveFile(File srcFile, File destFile) throws FileNotFoundException {
        boolean rename = srcFile.renameTo(destFile);
        if (!rename) {
            copyFile(srcFile.getAbsolutePath(), destFile.getAbsolutePath());
            deleteFile(srcFile.getAbsolutePath());
        }

    }

    public static boolean copyFile(String srcFilePath, String destFilePath) throws FileNotFoundException {
        InputStream inputStream = new FileInputStream(srcFilePath);
        return writeFile((String)destFilePath, (InputStream)inputStream);
    }

    public static boolean renameFile(File file, String newFileName) {
        File newFile = null;
        if (file.isDirectory()) {
            newFile = new File(file.getParentFile(), newFileName);
        } else {
            String temp = newFileName + file.getName().substring(file.getName().lastIndexOf(46));
            newFile = new File(file.getParentFile(), temp);
        }

        return file.renameTo(newFile);
    }

    public static String getFileNameWithoutSuffix(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return filePath;
        } else {
            int suffix = filePath.lastIndexOf(".");
            int fp = filePath.lastIndexOf(File.separator);
            if (fp == -1) {
                return suffix == -1 ? filePath : filePath.substring(0, suffix);
            } else if (suffix == -1) {
                return filePath.substring(fp + 1);
            } else {
                return fp < suffix ? filePath.substring(fp + 1, suffix) : filePath.substring(fp + 1);
            }
        }
    }

    public static String getFileName(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return filePath;
        } else {
            int fp = filePath.lastIndexOf(File.separator);
            return fp == -1 ? filePath : filePath.substring(fp + 1);
        }
    }

    public static String getFolderName(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return filePath;
        } else {
            int fp = filePath.lastIndexOf(File.separator);
            return fp == -1 ? "" : filePath.substring(0, fp);
        }
    }

    public static String getFileSuffix(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return filePath;
        } else {
            int suffix = filePath.lastIndexOf(".");
            int fp = filePath.lastIndexOf(File.separator);
            if (suffix == -1) {
                return "";
            } else {
                return fp >= suffix ? "" : filePath.substring(suffix + 1);
            }
        }
    }

    public static boolean makeDirs(String filePath) {
        String folderName = getFolderName(filePath);
        if (TextUtils.isEmpty(folderName)) {
            return false;
        } else {
            File folder = new File(folderName);
            return folder.exists() && folder.isDirectory() ? true : folder.mkdirs();
        }
    }

    public static boolean isFileExist(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return false;
        } else {
            File file = new File(filePath);
            return file.exists() && file.isFile();
        }
    }

    public static boolean isFolderExist(String directoryPath) {
        if (TextUtils.isEmpty(directoryPath)) {
            return false;
        } else {
            File dire = new File(directoryPath);
            return dire.exists() && dire.isDirectory();
        }
    }

    public static boolean deleteFile(String path) {
        if (TextUtils.isEmpty(path)) {
            return true;
        } else {
            File file = new File(path);
            if (!file.exists()) {
                return true;
            } else if (file.isFile()) {
                return file.delete();
            } else if (!file.isDirectory()) {
                return false;
            } else {
                if (file.isDirectory()) {
                    File[] var2 = file.listFiles();
                    int var3 = var2.length;

                    for(int var4 = 0; var4 < var3; ++var4) {
                        File f = var2[var4];
                        if (f.isFile()) {
                            f.delete();
                        } else if (f.isDirectory()) {
                            deleteFile(f.getAbsolutePath());
                        }
                    }
                }

                return file.delete();
            }
        }
    }

    public static boolean deleteFile(File file) {
        if (!file.exists()) {
            return true;
        } else if (file.isFile()) {
            return file.delete();
        } else if (!file.isDirectory()) {
            return false;
        } else {
            if (file.isDirectory()) {
                File[] childFile = file.listFiles();
                if (childFile == null || childFile.length == 0) {
                    return file.delete();
                }

                File[] var2 = childFile;
                int var3 = childFile.length;

                for(int var4 = 0; var4 < var3; ++var4) {
                    File f = var2[var4];
                    deleteFile(f);
                }
            }

            return file.delete();
        }
    }

    public static long getFileSize(String path) {
        if (TextUtils.isEmpty(path)) {
            return -1L;
        } else {
            File file = new File(path);
            return file.exists() && file.isFile() ? file.length() : -1L;
        }
    }

    public static long getFolderSize(File file) throws Exception {
        long size = 0L;

        try {
            File[] fileList = file.listFiles();

            for(int i = 0; i < fileList.length; ++i) {
                if (fileList[i].isDirectory()) {
                    size += getFolderSize(fileList[i]);
                } else {
                    size += fileList[i].length();
                }
            }
        } catch (Exception var5) {
            var5.printStackTrace();
        }

        return size;
    }

    public static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException var2) {
            }
        }

    }

    public static boolean deleteFilesInDirWithFilter(String dir, FileFilter filter) {
        return isSpace(dir) ? false : deleteFilesInDirWithFilter(new File(dir), filter);
    }

    public static boolean deleteFilesInDirWithFilter(File dir, FileFilter filter) {
        if (dir == null) {
            return false;
        } else if (!dir.exists()) {
            return true;
        } else if (!dir.isDirectory()) {
            return false;
        } else {
            File[] files = dir.listFiles();
            if (files != null && files.length != 0) {
                File[] var3 = files;
                int var4 = files.length;

                for(int var5 = 0; var5 < var4; ++var5) {
                    File file = var3[var5];
                    if (filter.accept(file)) {
                        if (file.isFile()) {
                            if (!file.delete()) {
                                return false;
                            }
                        } else if (file.isDirectory() && !deleteFile(file)) {
                            return false;
                        }
                    }
                }
            }

            return true;
        }
    }

    public static String getFileNameWithoutExtension(File file) {
        return file == null ? "" : getFileNameWithoutExtension(file.getPath());
    }

    public static String getFileNameWithoutExtension(String filePath) {
        if (isSpace(filePath)) {
            return "";
        } else {
            int lastPoi = filePath.lastIndexOf(46);
            int lastSep = filePath.lastIndexOf(File.separator);
            if (lastSep == -1) {
                return lastPoi == -1 ? filePath : filePath.substring(0, lastPoi);
            } else {
                return lastPoi != -1 && lastSep <= lastPoi ? filePath.substring(lastSep + 1, lastPoi) : filePath.substring(lastSep + 1);
            }
        }
    }

    private static boolean isSpace(String s) {
        if (s == null) {
            return true;
        } else {
            int i = 0;

            for(int len = s.length(); i < len; ++i) {
                if (!Character.isWhitespace(s.charAt(i))) {
                    return false;
                }
            }

            return true;
        }
    }
}
