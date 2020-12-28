package com.seray.sjc.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.seray.sjc.entity.device.PayTypeInfo;

import java.util.List;

@Dao
public interface PayTypeInfoDao {

    @Query("SELECT COUNT(*) FROM " + PayTypeInfo.TABLE_NAME)
    int count();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void save(List<PayTypeInfo> resources);

    @Query("SELECT * FROM " + PayTypeInfo.TABLE_NAME)
    List<PayTypeInfo> loadAll();

    @Query("DELETE FROM " + PayTypeInfo.TABLE_NAME)
    void delete();

}
