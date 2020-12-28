package com.seray.sjc.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import com.seray.sjc.annotation.TransType;
import com.seray.sjc.annotation.UploadStatus;
import com.seray.sjc.entity.order.SjcSubtotal;
import com.seray.sjc.entity.report.ReportSubtotal;
import com.seray.sjc.entity.report.SjcSubtotalWithSjcDetail;

import java.util.List;

/**
 * Author：李程
 * CreateTime：2019/4/13 14:21
 * E-mail：licheng@kedacom.com
 * Describe：世界村小计数据库操作类
 */
@Dao
public interface SjcSubtotalDao extends IDataCleanWork {

    @Query("SELECT COUNT(0) FROM " + SjcSubtotal.TABLE_NAME)
    int count();

    /**
     * 清理三十天前已上传成功的订单
     */
    @Query("DELETE FROM " + SjcSubtotal.TABLE_NAME + " WHERE uploadStatus = 1 AND STRFTIME('%m-%d',transDate) < STRFTIME('%m-%d','now','-30')")
    void autoClean();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void save(SjcSubtotal subtotal);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void save(List<SjcSubtotal> subtotals);

    @Query("DELETE FROM " + SjcSubtotal.TABLE_NAME + " WHERE transOrderCode =:transOrderCode")
    void delete(String transOrderCode);

    @Query("SELECT * FROM " + SjcSubtotal.TABLE_NAME)
    List<SjcSubtotal> loadAll();

    @Query("SELECT * FROM " + SjcSubtotal.TABLE_NAME + " WHERE uploadStatus = " + UploadStatus.NO
            + " AND ((payStatus = 2 AND transType = " + TransType.NORMAL + ") OR (payStatus = 0 AND transType = " + TransType.FORCE_RECORD + ")) LIMIT :maxCount")
    List<SjcSubtotal> getNoUploadOrdersOneTime(int maxCount);

    @Query("UPDATE " + SjcSubtotal.TABLE_NAME + " SET uploadStatus =:uploadStatus WHERE transOrderCode =:transOrderCode")
    void updateUploadStatus(String transOrderCode, int uploadStatus);

    /**
     * 支付状态 -1：支付失败 0：支付中 1：支付取消 2：支付成功
     *
     * @param transOrderCode
     */
    @Query("UPDATE " + SjcSubtotal.TABLE_NAME + " SET payStatus=:payStatus WHERE transOrderCode =:transOrderCode")
    // 支付中 支付成功 支付取消 支付失败
    void updatePayStatus(String transOrderCode, int payStatus);

    /**
     * 获取报表数据
     *
     * @param startDateTime 开始时间
     * @param endDateTime   结束时间
     * @return
     */
    @Query("SELECT SUM(transAmt) AS transAmtSum,payType,COUNT(*) AS count " +
            "FROM " + SjcSubtotal.TABLE_NAME + "  WHERE payStatus=2 AND transDate>=:startDateTime AND transDate<=:endDateTime" +
            " GROUP BY payType  ORDER BY SUM(transAmt) DESC")
    List<ReportSubtotal> getReportSubtotal(String startDateTime, String endDateTime);

    /**
     * 获取商品小计
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return
     */
    @Transaction
    @Query("SELECT * FROM " + SjcSubtotal.TABLE_NAME + " WHERE  payStatus = 2 AND transDate>=:startTime AND transDate<=:endTime ")
    List<SjcSubtotalWithSjcDetail> loadSubtotalByTime(String startTime, String endTime);

    /**
     * 获取商品商品小计
     */
    @Query("SELECT * FROM " + SjcSubtotal.TABLE_NAME + " WHERE payStatus = 2 AND transOrderCode=:id")
    @Transaction
    SjcSubtotalWithSjcDetail loadSjdSubtotalWithDetail(String id);

    @Query("DELETE FROM " + SjcSubtotal.TABLE_NAME)
    void delete();

}
