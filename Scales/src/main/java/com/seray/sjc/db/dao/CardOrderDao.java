package com.seray.sjc.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.seray.sjc.annotation.UploadStatus;
import com.seray.sjc.entity.card.CardPayOrder;

/**
 * Author：李程
 * CreateTime：2019/5/8 10:39
 * E-mail：licheng@kedacom.com
 * Describe：世界村预付卡交易报文数据库操作类
 */
@Dao
public interface CardOrderDao extends IDataCleanWork {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void save(CardPayOrder order);

    @Query("UPDATE " + CardPayOrder.TABLE_NAME + " SET uploadStatus =:uploadStatus WHERE transOrderCode =:transOrderCode")
    void updateUploadStatus(String transOrderCode, int uploadStatus);

    @Query("DELETE FROM " + CardPayOrder.TABLE_NAME + " WHERE uploadStatus = " + UploadStatus.SUCCESS)
    void autoClean();

    @Query("DELETE FROM " + CardPayOrder.TABLE_NAME)
    void delete();

}
