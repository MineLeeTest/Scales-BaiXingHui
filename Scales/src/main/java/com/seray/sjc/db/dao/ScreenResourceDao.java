package com.seray.sjc.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.seray.sjc.entity.device.ScreenResource;

import java.util.List;

/**
 * Author：李程
 * CreateTime：2019/4/18 21:31
 * E-mail：licheng@kedacom.com
 * Describe：世界村后显示资源数据库操作类
 */
@Dao
public interface ScreenResourceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void save(List<ScreenResource> resources);

    @Query("SELECT * FROM " + ScreenResource.TABLE_NAME)
    List<ScreenResource> loadAll();

    @Query("SELECT COUNT(*) FROM " + ScreenResource.TABLE_NAME + " WHERE startDate <= STRFTIME('%s','now') * 1000 AND endDate >= STRFTIME('%s','now') * 1000")
    int countUsableResource();

    @Query("SELECT COUNT(*) FROM " + ScreenResource.TABLE_NAME + " WHERE resourceType = 1 AND startDate <= STRFTIME('%s','now') * 1000 AND endDate >= STRFTIME('%s','now') * 1000")
    int countUsablePictures();

    @Query("SELECT COUNT(*) FROM " + ScreenResource.TABLE_NAME + " WHERE resourceType = 2 AND startDate <= STRFTIME('%s','now') * 1000 AND endDate >= STRFTIME('%s','now') * 1000")
    int countUsableVideos();

    @Query("SELECT * FROM " + ScreenResource.TABLE_NAME + " WHERE resourceType = 1 AND startDate <= STRFTIME('%s','now') * 1000 AND endDate >= STRFTIME('%s','now') * 1000")
    List<ScreenResource> loadPictures();

    @Query("SELECT * FROM " + ScreenResource.TABLE_NAME + " WHERE resourceType = 2 AND startDate <= STRFTIME('%s','now') * 1000 AND endDate >= STRFTIME('%s','now') * 1000")
    List<ScreenResource> loadVideos();

    @Query("DELETE FROM " + ScreenResource.TABLE_NAME)
    void delete();

}
