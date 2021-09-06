package com.seray.log;


import android.support.annotation.NonNull;
import android.support.annotation.StringDef;

import com.seray.scaleviewlib.utils.Utils;
import com.seray.util.FileUtils;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import de.mindpipe.android.logging.log4j.LogCatAppender;


/**
 * 日志
 * 打印和写入
 *
 * @author licheng
 * @since 2019/9/23 10:47
 */
public class LLog {

    /**
     * 控制台打印的内容格式.
     */
    private static final String PRINT_CONSOLE_FORMAT = "[(%1$s:%2$d)#%3$s]";

    private LLog() {
        super();
    }


    /**
     * 初始化
     *
     * @param isDebug       是否将日志打印到控制台
     * @param isWriteToFile 是否将日志记录到本地文本中
     * @param autoClearDay  自动销毁{@code autoClearDay}天前的日志文件，如果为0或者负数，则不销毁
     */
    public static void init(boolean isDebug, boolean isWriteToFile, int autoClearDay) {
        configure(isDebug, isWriteToFile);
        if (autoClearDay > 0) {
            autoClear(autoClearDay);
        }
    }

    private static void autoClear(final int autoClearDay) {
        String k5SystemLogDirectoryPath = FileIOUtils.getSystemLogDirectoryPath();
        FileUtils.deleteFilesInDirWithFilter(k5SystemLogDirectoryPath, pathname -> {
            String s = FileUtils.getFileNameWithoutExtension(pathname);
            int day = autoClearDay < 0 ? autoClearDay : -1 * autoClearDay;
            String date = getOtherDay(day);
            int compareTo = date.compareTo(s);
            return compareTo >= 0;
        });
    }

    private static String getOtherDay(int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(System.currentTimeMillis()));
        calendar.add(Calendar.DAY_OF_YEAR, day);
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return formatter.format(calendar.getTime());
    }

//    public static void setWriteToFile(boolean writeToFile) {
//        Appender appender = getLogger().getAppender(UserDailyRollingFileAppender.USER_LOG_APPENDER_NAME);
//        if (writeToFile) {
//            if (appender != null) return;
//            addUserLogAppender(getLogger(), getPatternLayout());
//        } else {
//            getLogger().removeAppender(UserDailyRollingFileAppender.USER_LOG_APPENDER_NAME);
//        }
//    }

    private static Logger getLogger() {
        return Logger.getRootLogger();
    }

    public static void clickLog(String log) {
        info(LogTag.CLICK + "-" + log);
    }

    public static void activityLog(String log) {
        info(LogTag.ACTIVITY + "-" + log);
    }

    /**
     * 输出或保存debug日志
     *
     * @param log 内容
     */
    public static void debug(String log) {
        String functionName = getFunctionName();
        getLogger().debug(functionName + "-" + log);
    }

    /**
     * 输出或保存debug日志
     *
     * @param tag 日志TAG
     * @param log 内容
     */
    public static void debug(String tag, String log) {
        String functionName = getFunctionName();
        getLogger().debug(functionName + "-" + tag + "-" + log);
    }

    /**
     * 输出或保存info日志
     *
     * @param log 内容
     */
    public static void info(String log) {
        getLogger().info(log);
    }

    public static void i(String log) {
        getLogger().info(log);
    }

    /**
     * 输出或保存info日志
     *
     * @param tag 日志TAG
     * @param log 内容
     */
    public static void info(String tag, String log) {
        info(tag + "-" + log);
    }

    /**
     * 输出或保存error日志
     *
     * @param log 内容
     */
    public static void error(String log) {
        String functionName = getFunctionName();
        getLogger().error(functionName + "-" + log);
    }

    public static void e(String log) {
        String functionName = getFunctionName();
        getLogger().error(functionName + "-" + log);
    }

    public static void e(String log, String msg) {
        String functionName = getFunctionName();
        getLogger().error(functionName + "-" + log + "-->" + msg);
    }

    public static void e(String log, Exception e) {
        String functionName = getFunctionName();
        getLogger().error(functionName + "-" + log + "-->" + e.getMessage());
    }

    /**
     * 输出或保存error日志
     *
     * @param tag 日志TAG
     * @param log 内容
     */
    public static void error(String tag, String log) {
        String functionName = getFunctionName();
        getLogger().error(functionName + "-" + tag + "-" + log);
    }

    /**
     * 获取系统换行符
     *
     * @return 系统换行符
     */
    private static String getLineSeparator() {
        return System.getProperty("line.separator");
    }

    /**
     * 获取方法名
     */
    private static String getFunctionName() {
        StackTraceElement[] sts = Thread.currentThread().getStackTrace();
        if (sts == null) {
            return null;
        }
        for (StackTraceElement st : sts) {
            if (st.isNativeMethod()) {
                continue;
            }
            if (st.getClassName().equals(Thread.class.getName())) {
                continue;
            }
            if (st.getClassName().equals(LLog.class.getName())) {
                continue;
            }
            return decorateMsgForConsole(st);
        }
        return null;
    }


    private static void configure(boolean isDebug, boolean isWriteToFile) {
        Logger rootLogger = Logger.getRootLogger();
        PatternLayout layout = getPatternLayout();
        RollingFileAppender debugAppender = new RollingFileAppender();
        debugAppender.setLayout(layout);
        debugAppender.setAppend(true);
        String sdCardPathByEnvironment = FileIOUtils.getSDCardPathByEnvironment();
        String packageName = Utils.getApp().getPackageName();
        debugAppender.setFile(sdCardPathByEnvironment + File.separator + packageName + ".log" + File.separator + "edx_printer.log");
        debugAppender.setThreshold(Level.DEBUG);
        debugAppender.setMaxFileSize("10MB");
        debugAppender.setMaxBackupIndex(50);
        debugAppender.setImmediateFlush(true);
        debugAppender.activateOptions();
        rootLogger.addAppender(debugAppender);

        if (isDebug) {
            LogCatAppender logCatAppender = new LogCatAppender(layout);
            rootLogger.setLevel(Level.DEBUG);
            rootLogger.addAppender(logCatAppender);
        } else {
            ConsoleAppender consoleAppender = new ConsoleAppender(layout, "System.out");
            consoleAppender.setThreshold(Level.DEBUG);
            rootLogger.addAppender(consoleAppender);
        }

//        if (isWriteToFile) {
//            addUserLogAppender(rootLogger, layout);
//        }

//        rootLogger.setLevel(isDebug ? Level.DEBUG : Level.INFO);

        rootLogger.setLevel(Level.DEBUG);
    }

    private static PatternLayout getPatternLayout() {
        return new PatternLayout("[%d{yyyy-MM-dd HH:mm:ss.SSS}]-[%p]-%m%n");
    }

//    private static void addUserLogAppender(Logger rootLogger, PatternLayout layout) {
//        String k5SystemLogDirectoryPath = FileIOUtils.getSystemLogDirectoryPath();
//        Log.d("FileIOUtils", k5SystemLogDirectoryPath);
//        UserDailyRollingFileAppender userAppender = new UserDailyRollingFileAppender();
//        userAppender.setFile(k5SystemLogDirectoryPath + "log");
//        userAppender.setLayout(layout);
//        userAppender.activateOptions();
//        rootLogger.addAppender(userAppender);
//    }

    /**
     * 装饰打印到控制台的信息.
     *
     * @param element 对战元素
     * @return 装饰后的信息
     */
    private static String decorateMsgForConsole(@NonNull StackTraceElement element) {
        String methodName = element.getMethodName();
        int lineNumber = element.getLineNumber();
        String fileName = element.getFileName();
        return String.format(Locale.getDefault(), PRINT_CONSOLE_FORMAT, fileName, lineNumber, methodName);
    }

    @StringDef({
            LogTag.CLICK,
            LogTag.ACTIVITY
    })
    public @interface LogTag {
        /**
         * 点击事件的
         */
        String CLICK = "点击按钮";

        /**
         * 页面创建或销毁
         */
        String ACTIVITY = "页面活动";
    }
}