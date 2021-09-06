package com.seray.sjc.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.seray.sjc.entity.device.ConfigADB;
import com.seray.sjc.entity.device.ProductADB;

import java.util.List;

/**
 * Author：李程
 * CreateTime：2019/5/2 19:02
 * E-mail：licheng@kedacom.com
 * Describe：
 */
@Dao
public interface ProductDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void save(ProductADB productADB);

    @Query("SELECT * FROM " + ProductADB.TABLE_NAME+" order by sequences desc")
    List<ProductADB> loadAll();

    @Query("SELECT * FROM " + ProductADB.TABLE_NAME + " WHERE  'product_id' =:product_id")
    ProductADB findByID(Integer product_id);

    @Query("DELETE FROM " + ProductADB.TABLE_NAME)
    void delete();
}
