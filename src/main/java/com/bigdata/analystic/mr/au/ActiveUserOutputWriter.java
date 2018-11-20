package com.bigdata.analystic.mr.au;

import com.bigdata.analystic.model.StatsBaseDimension;
import com.bigdata.analystic.model.StatsUserDimension;
import com.bigdata.analystic.model.value.StatsOutputValue;
import com.bigdata.analystic.model.value.reduce.OutputWritable;
import com.bigdata.analystic.mr.IOutputWriter;
import com.bigdata.analystic.mr.service.IDimension;
import com.bigdata.common.GlobalConstants;
import com.bigdata.common.KpiType;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.IntWritable;
import org.apache.log4j.Logger;

import java.sql.PreparedStatement;

/**
 * @ClassName: ActiveUserOutputWriter
 * @Description: TODO  〈一句话功能简述〉<br>〈对于不同的指标，这列赋值都是不一样的〉
 * @Author: xqg
 * @Date: 2018/11/7 14:28
 */
public class ActiveUserOutputWriter implements IOutputWriter {
    private static final Logger logger = Logger.getLogger(ActiveUserOutputWriter.class);

    @Override
    //这里通过key和value给ps语句赋值
    public void output(Configuration conf, StatsBaseDimension key, StatsOutputValue value, PreparedStatement ps, IDimension iDimension) {

        try {
            StatsUserDimension k = (StatsUserDimension) key;
            OutputWritable v = (OutputWritable) value;
            int i = 0;
            switch (v.getKpi()) {
                case ACTIVE_USER:
                case BROWSER_ACTIVE_USER:
                    //获取新增用户的值
                    int ActiveUser = ((IntWritable) (v.getValue().get(new IntWritable(-1)))).get();
                    // System.out.println(ActiveUser);
                    //时间维度
                    ps.setInt(++i, iDimension.getDimensionIdByObject(k.getStatsCommonDimension().getDateDimension()));
                    //平台维度
                    ps.setInt(++i, iDimension.getDimensionIdByObject(k.getStatsCommonDimension().getPlatformDimension()));

                    //TODO 修改1--浏览器活跃用户
                    if (v.getKpi().equals(KpiType.BROWSER_ACTIVE_USER)) {
                        ps.setInt(++i, iDimension.getDimensionIdByObject(k.getBrowserDimension()));
                    }

                    ps.setInt(++i, ActiveUser);
                    //注意这里需要在runner类里面进行赋值
                    ps.setString(++i, conf.get(GlobalConstants.RUNNING_DATE));
                    ps.setInt(++i, ActiveUser);
                    break;

                case HOURLY_ACTIVE_USER:
                    //时间维度
                    ps.setInt(++i, iDimension.getDimensionIdByObject(k.getStatsCommonDimension().getDateDimension()));
                    //平台维度
                    ps.setInt(++i, iDimension.getDimensionIdByObject(k.getStatsCommonDimension().getPlatformDimension()));
                    //kpi维度
                    ps.setInt(++i, iDimension.getDimensionIdByObject(k.getStatsCommonDimension().getKpiDimension()));
                    for (int j = 0; j < 24; j++) {

                        ps.setInt(++i, ((IntWritable) (v.getValue().get(new IntWritable(j)))).get());
                    }
//
                    //注意这里需要在runner类里面进行赋值
                    ps.setString(++i, conf.get(GlobalConstants.RUNNING_DATE));
                    for (int j = 0; j < 24; j++) {
                        ps.setInt(++i, ((IntWritable) (v.getValue().get(new IntWritable(j)))).get());
                    }
                    break;
            }

            ps.addBatch();//添加到批处理中，批量执行SQL语句
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("给ps赋值失败！！！");
        }
    }
}