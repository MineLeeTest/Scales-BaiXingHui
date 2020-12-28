package com.seray.sjc.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.seray.sjc.entity.product.SjcCategory;

import java.util.List;

/**
 * Author：李程
 * CreateTime：2019/4/13 14:23
 * E-mail：licheng@kedacom.com
 * Describe：世界村商品分类数据库操作类
 */
@Dao
public interface SjcCategoryDao {

    @Query("SELECT COUNT(0) FROM " + SjcCategory.TABLE_NAME)
    int count();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void save(List<SjcCategory> categories);

    @Query("SELECT * FROM " + SjcCategory.TABLE_NAME + " ORDER BY categorySort")
    List<SjcCategory> loadAll();

    @Query("DELETE FROM " + SjcCategory.TABLE_NAME)
    void delete();

}
