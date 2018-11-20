package com.bigdata.analystic.hive;

import com.bigdata.Util.TimeUtil;
import com.bigdata.analystic.model.base.DateDimension;
import com.bigdata.analystic.mr.service.IDimension;
import com.bigdata.analystic.mr.service.implement.IDimensionImpl;
import com.bigdata.common.DateEnum;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hive.ql.exec.UDF;

import java.io.IOException;
import java.sql.SQLException;

/**
 * @ClassName: DateDimensionUdf
 * @Description: TODO 获取时间维度的ID
 * @Author: xqg
 * @Date: 2018/11/17 0:24
 */
public class DateDimensionUdf extends UDF {
    IDimension iDimension = new IDimensionImpl();

    /**
     * @param date
     * @return 获取时间的ID
     */
    public int evaluate(String date) {
        if (StringUtils.isEmpty(date)) {
            date = TimeUtil.getYesterday();
        }
        DateDimension dateDimension = DateDimension.buildDate(TimeUtil.parseString2Long(date), DateEnum.DAY);
        int id = 0;
        try {
            id = iDimension.getDimensionIdByObject(dateDimension);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return id;
    }

    public static void main(String[] args) {
        System.out.println(new DateDimensionUdf().evaluate("2018-11-17"));
    }
}