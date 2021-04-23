package com.seray.util;

import android.text.TextUtils;
import android.util.Log;

import java.util.Date;


/**
 * 调试Log工具类
 */
public class LogUtil {
    public static final int NOTHING = 6;
    private static final int VERBOSE = 1;
    private static final int DEBUG = 2;
    private static final int INFO = 3;
    private static final int WARN = 4;
    private static final int ERROR = 5;
    private static final int LEVEL = VERBOSE;
    private static final String SEPARATOR = ",";

    public static void v(String message) {
        if (LEVEL <= VERBOSE) {
            StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
            String tag = getDefaultTag(stackTraceElement);
            Log.v(tag, getLogInfo(stackTraceElement) + message);
        }
    }

    private static String getDefaultTag(StackTraceElement stackTraceElement) {
        String fileName = stackTraceElement.getFileName();
        String stringArray[] = fileName.split("\\.");
        return stringArray[0];
    }

    private static String getLogInfo(StackTraceElement stackTraceElement) {
        StringBuilder logInfoStringBuilder = new StringBuilder();
        // 获取线程名
        String threadName = Thread.currentThread().getName();
        // 获取类名.即包名+类名
        String className = stackTraceElement.getClassName();
        // 获取方法名称
        String methodName = stackTraceElement.getMethodName();
        // 获取生日输出行数
        int lineNumber = stackTraceElement.getLineNumber();

        logInfoStringBuilder.append("[ ");
        logInfoStringBuilder.append("Thread=").append(threadName).append(SEPARATOR);
        logInfoStringBuilder.append("Class=").append(className).append(SEPARATOR);
        logInfoStringBuilder.append("Method=").append(methodName).append(SEPARATOR);
        logInfoStringBuilder.append("Line").append(lineNumber);
        logInfoStringBuilder.append(" ] ");
        return logInfoStringBuilder.toString();
    }

    public static void v(String tag, String message) {
        if (LEVEL <= VERBOSE) {
            StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
            if (TextUtils.isEmpty(tag)) {
                tag = getDefaultTag(stackTraceElement);
            }
            Log.v(tag, getLogInfo(stackTraceElement) + "--" + new Date().toString() + "--" + message);
        }
    }

    public static void d(String message) {
        if (LEVEL <= DEBUG) {
            StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
            String tag = getDefaultTag(stackTraceElement);
            Log.d(tag, getLogInfo(stackTraceElement) + "--" + new Date().toString() + "--" + message);
        }
    }

    public static void d(String tag, String message) {
        if (LEVEL <= DEBUG) {
            StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
            if (TextUtils.isEmpty(tag)) {
                tag = getDefaultTag(stackTraceElement);
            }
            Log.d(tag, getLogInfo(stackTraceElement) + "--" + new Date().toString() + "--" + message);
        }
    }

    public static void i(String message) {
//        if (LEVEL <= INFO) {
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
        String tag = getDefaultTag(stackTraceElement);
        String info = new Date().toString() + "--" + message;
        Log.i(tag, getLogInfo(stackTraceElement) + "--" + info);
//        }
    }

    public static void i(String tag, String message) {
        if (LEVEL <= INFO) {
            StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
            if (TextUtils.isEmpty(tag)) {
                tag = getDefaultTag(stackTraceElement);
            }
            Log.i(tag, getLogInfo(stackTraceElement) + "--" + new Date().toString() + "--" + message);
        }
    }

    public static void w(String message) {
        if (LEVEL <= WARN) {
            StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
            String tag = getDefaultTag(stackTraceElement);
            Log.w(tag, getLogInfo(stackTraceElement) + "--" + new Date().toString() + "--" + message);
        }
    }

    public static void w(String tag, String message) {
        if (LEVEL <= WARN) {
            StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
            if (TextUtils.isEmpty(tag)) {
                tag = getDefaultTag(stackTraceElement);
            }
            Log.w(tag, getLogInfo(stackTraceElement) + "--" + new Date().toString() + "--" + message);
        }
    }

    public static void e(String tag, String message) {
        if (LEVEL <= ERROR) {
            StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
            if (TextUtils.isEmpty(tag)) {
                tag = getDefaultTag(stackTraceElement);
            }
            Log.e(tag, getLogInfo(stackTraceElement) + "--" + new Date().toString() + "--" + message);
        }
    }

    public static void e(String message) {
        if (LEVEL <= ERROR) {
            StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
            String tag = getDefaultTag(stackTraceElement);
            Log.e(tag, getLogInfo(stackTraceElement) + "--" + new Date().toString() + "--" + message);
        }
    }

    public static void l(String tag, String msg) {
        Log.i(tag, "--" + new Date().toString() + "--" + msg);
    }
}
