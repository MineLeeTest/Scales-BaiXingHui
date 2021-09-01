package com.seray.log;


import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.Level;

/**
 * @author licheng
 * @since 2020/3/19 14:09
 */
class UserDailyRollingFileAppender extends DailyRollingFileAppender {

    static final String USER_LOG_APPENDER_NAME = "USER_LOG_APPENDER";

    UserDailyRollingFileAppender() {
        name = USER_LOG_APPENDER_NAME;
        fileAppend = true;
        threshold = Level.INFO;
        setDatePattern("'-'yyyyMMdd'.log'");
    }
//    /**
//     * 只记录设置的日志等级的日志
//     */
//    @Override
//    public boolean isAsSevereAsThreshold(Priority priority) {
//        Priority threshold = getThreshold();
//        return ((threshold == null) || threshold.equals(priority));
//    }
}