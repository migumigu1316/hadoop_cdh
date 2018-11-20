package com.bigdata.analystic.mr.nm;

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
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.log4j.Logger;

import java.sql.PreparedStatement;

/**
 * @ClassName: NewMemberOutputWriter
 * @Description: TODO 〈一句话功能简述〉<br>〈对于不同的指标，这列赋值都是不一样的〉
 * @Author: xqg
 * @Date: 2018/11/8 14:28
 */
public class NewMemberOutputWriter implements IOutputWriter {
    private static final Logger logger = Logger.getLogger(NewMemberOutputWriter.class);
    @Override
    //这里通过key和value给ps语句赋值
    public void output(Configuration conf, StatsBaseDimension key,
                       StatsOutputValue value, PreparedStatement ps, IDimension iDimension) {

        try {
            StatsUserDimension k = (StatsUserDimension) key;
            OutputWritable v = (OutputWritable) value;
            int i = 0;
            switch (v.getKpi()){
                case NEW_MEMBER:
                case BROWSER_NEW_MEMBER:
                    //获取会员用户的值
                    int NewMember = ((IntWritable) (v.getValue().get(new IntWritable(-1)))).get();
                    ps.setInt(++i, iDimension.getDimensionIdByObject(k.getStatsCommonDimension().getDateDimension()));
                    ps.setInt(++i, iDimension.getDimensionIdByObject(k.getStatsCommonDimension().getPlatformDimension()));

                    //修改1--浏览器新增会员   〈对于不同的指标，这列赋值都是不一样的〉
                    if (v.getKpi().equals(KpiType.BROWSER_NEW_MEMBER)) {
                        ps.setInt(++i, iDimension.getDimensionIdByObject(k.getBrowserDimension()));
                    }

                    ps.setInt(++i, NewMember);
                    ps.setString(++i, conf.get(GlobalConstants.RUNNING_DATE));//注意这里需要在runner类里面进行赋值
                    ps.setInt(++i, NewMember);
//                    System.out.println(ps);
                    break;

                case MEMBER_INFO:
//                    System.out.println("--------------调试位置:找ps赋值失败---------------");
                    String memberId = ((Text) v.getValue().get(new IntWritable(-2))).toString();
                    long minTime = ((LongWritable)(v.getValue().get(new IntWritable(-3)))).get();
                    ps.setString(++i,memberId);
                    ps.setString(++i,conf.get(GlobalConstants.RUNNING_DATE));
//                    ps.setLong(++i, TimeUtil.parseString2Long(conf.get(GlobalConstants.RUNNING_DATE)));
                    ps.setLong(++i,minTime);
                    ps.setString(++i,conf.get(GlobalConstants.RUNNING_DATE));
                    ps.setString(++i,conf.get(GlobalConstants.RUNNING_DATE));
                    break;
                default:
                    throw new RuntimeException("找不到kpi");
            }
            ps.addBatch();//添加到批处理中，批量执行SQL语句
        } catch (Exception e) {
            logger.warn("给ps赋值失败！！！");
        }
    }
}