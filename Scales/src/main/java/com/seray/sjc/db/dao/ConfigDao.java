package com.seray.sjc.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.seray.sjc.entity.device.Config;

import java.util.List;

/**
 * Author：李程
 * CreateTime：2019/5/2 19:02
 * E-mail：licheng@kedacom.com
 * Describe：
 */
@Dao
public interface ConfigDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void save(Config config);

    @Query("SELECT * FROM " + Config.TABLE_NAME)
    List<Config> loadAll();

    @Query("SELECT configValue FROM " + Config.TABLE_NAME + " WHERE configKey =:configKey LIMIT 1")
    String get(String configKey);

    @Query("DELETE FROM " + Config.TABLE_NAME)
    void delete();
}
