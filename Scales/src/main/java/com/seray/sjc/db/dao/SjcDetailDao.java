package com.seray.sjc.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.seray.sjc.entity.report.ReportDetail;
import com.seray.sjc.entity.order.SjcDetail;

import java.util.List;

/**
 * Author：李程
 * CreateTime：2019/4/13 14:22
 * E-mail：licheng@kedacom.com
 * Describe：世界村明细数据库操作类
 */
@Dao
public interface SjcDetailDao {

    @Query("SELECT COUNT(0) FROM " + SjcDetail.TABLE_NAME)
    int count();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void save(List<SjcDetail> details);

    @Query("SELECT * FROM " + SjcDetail.TABLE_NAME + " WHERE transOrderCode =:transOrderCode")
    List<SjcDetail> loadByTransOrderCode(String transOrderCode);

    /**
     * 获取报表数据
     */
    @Query("SELECT a.goodsName,SUM(a.dealAmt) AS dealAmtSum,a.priceType,SUM(a.dealCnt) AS dealCntSum " +
            "FROM Detail AS a WHERE a.transOrderCode IN (SELECT b.transOrderCode " +
            "FROM Subtotal AS b  WHERE transDate>=:startDateTime AND transDate<=:endDateTime) " +
            "GROUP BY a.goodsName ,a.priceType ORDER BY SUM(a.dealAmt) DESC,(a.goodsName) DESC")
    List<ReportDetail> getReportDetail(String startDateTime, String endDateTime);

}
