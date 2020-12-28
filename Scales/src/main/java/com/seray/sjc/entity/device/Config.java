package com.seray.sjc.entity.device;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.UUID;

/**
 * Author：李程
 * CreateTime：2019/5/2 19:01
 * E-mail：licheng@kedacom.com
 * Describe：配置表
 */
@Entity(tableName = Config.TABLE_NAME)
public class Config {

    public static final String TABLE_NAME = "Config";

    @ColumnInfo(name = "configKey")
    @PrimaryKey
    @NonNull
    private String configKey = UUID.randomUUID().toString();

    @ColumnInfo(name = "configValue")
    private String configValue;

    public Config() {
    }

    @Ignore
    public Config(@NonNull String configKey, String configValue) {
        this.configKey = configKey;
        this.configValue = configValue;
    }

    @NonNull
    public String getConfigKey() {
        return configKey;
    }

    public void setConfigKey(@NonNull String configKey) {
        this.configKey = configKey;
    }

    public String getConfigValue() {
        return configValue;
    }

    public void setConfigValue(String configValue) {
        this.configValue = configValue;
    }

    @Override
    public String toString() {
        return "Config{" +
                "configKey='" + configKey + '\'' +
                ", configValue='" + configValue + '\'' +
                '}';
    }
}
