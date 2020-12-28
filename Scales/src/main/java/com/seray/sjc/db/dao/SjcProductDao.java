package com.seray.sjc.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.seray.sjc.entity.product.SjcProduct;

import java.util.List;

/**
 * Author：李程
 * CreateTime：2019/4/13 14:23
 * E-mail：licheng@kedacom.com
 * Describe：世界村商品数据库操作类
 */
@Dao
public interface SjcProductDao {

    @Query("SELECT COUNT(0) FROM " + SjcProduct.TABLE_NAME)
    int count();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void save(List<SjcProduct> products);

    @Query("SELECT * FROM " + SjcProduct.TABLE_NAME + " WHERE categoryId =:categoryId ORDER BY goodsSort")
    List<SjcProduct> loadProductByCategoryId(String categoryId);

    @Query("SELECT * FROM " + SjcProduct.TABLE_NAME + " ORDER BY goodsSort")
    List<SjcProduct> loadAll();

    @Query("SELECT * FROM " + SjcProduct.TABLE_NAME + " WHERE goodsName LIKE :name")
    List<SjcProduct> similarProduct(String name);

    @Query("DELETE FROM " + SjcProduct.TABLE_NAME)
    void delete();

}
