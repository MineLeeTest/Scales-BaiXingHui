package com.seray.sjc.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.seray.sjc.entity.device.ConfigADB;

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
    void save(ConfigADB configADB);

    @Query("SELECT * FROM " + ConfigADB.TABLE_NAME)
    List<ConfigADB> loadAll();

    @Query("SELECT 'configValue' FROM " + ConfigADB.TABLE_NAME + " WHERE 'configKey' =:configKey LIMIT 1")
    String get(String configKey);

    @Query("DELETE FROM " + ConfigADB.TABLE_NAME)
    void delete();
}
