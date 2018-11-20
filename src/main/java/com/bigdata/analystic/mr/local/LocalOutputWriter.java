package com.bigdata.analystic.mr.local;

import com.bigdata.analystic.model.StatsBaseDimension;
import com.bigdata.analystic.model.StatsLocationDimension;
import com.bigdata.analystic.model.value.StatsOutputValue;
import com.bigdata.analystic.model.value.reduce.LocationReduceOutput;
import com.bigdata.analystic.mr.IOutputWriter;
import com.bigdata.analystic.mr.service.IDimension;
import com.bigdata.common.GlobalConstants;
import org.apache.hadoop.conf.Configuration;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @ClassName: LocalOutputWriter
 * @Description: TODO local的ps的赋值
 * @Author: xqg
 * @Date: 2018/11/16 15:44
 */
public class LocalOutputWriter implements IOutputWriter {
    @Override
    public void output(Configuration conf, StatsBaseDimension key, StatsOutputValue value, PreparedStatement ps, IDimension iDimension) {
        try {
            StatsLocationDimension statsLocationDimension  = (StatsLocationDimension) key;
            LocationReduceOutput locationReduceOutput = (LocationReduceOutput) value;
            //为ps赋值
            int i = 0;
            ps.setInt(++i,iDimension.getDimensionIdByObject(statsLocationDimension.getStatsCommonDimension().getDateDimension()));
            ps.setInt(++i,iDimension.getDimensionIdByObject(statsLocationDimension.getStatsCommonDimension().getPlatformDimension()));
            ps.setInt(++i,iDimension.getDimensionIdByObject(statsLocationDimension.getLocationDimension()));
            ps.setInt(++i,locationReduceOutput.getAus());
            ps.setInt(++i,locationReduceOutput.getSession());
            ps.setInt(++i,locationReduceOutput.getBounce_session());
            ps.setString(++i,conf.get(GlobalConstants.RUNNING_DATE));
            ps.setInt(++i,locationReduceOutput.getAus());
            ps.setInt(++i,locationReduceOutput.getSession());
            ps.setInt(++i,locationReduceOutput.getBounce_session());

            //添加到批处理中
            ps.addBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}