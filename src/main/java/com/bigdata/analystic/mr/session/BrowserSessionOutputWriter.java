//package com.bigdata.analystic.mr.session;
//
//import com.bigdata.analystic.model.StatsBaseDimension;
//import com.bigdata.analystic.model.StatsUserDimension;
//import com.bigdata.analystic.model.value.StatsOutputValue;
//import com.bigdata.analystic.model.value.reduce.OutputWritable;
//import com.bigdata.analystic.mr.IOutputWriter;
//import com.bigdata.analystic.mr.service.IDimension;
//import com.bigdata.common.GlobalConstants;
//import com.bigdata.common.KpiType;
//import org.apache.hadoop.conf.Configuration;
//import org.apache.hadoop.io.IntWritable;
//import org.apache.log4j.Logger;
//
//import java.sql.PreparedStatement;
//
///**
// * @ClassName: BrowserSessionOutputWriter
// * @Description: TODO  〈一句话功能简述〉<br>〈对于不同的指标，这列赋值都是不一样的〉
// * @Author: xqg
// * @Date: 2018/11/7 15:10
// */
//public class BrowserSessionOutputWriter implements IOutputWriter {
//    private static final Logger logger = Logger.getLogger(BrowserSessionOutputWriter.class);
//
//    @Override
//    //这里通过key和value给ps语句赋值
//    public void output(Configuration conf, StatsBaseDimension key, StatsOutputValue value, PreparedStatement ps, IDimension iDimension) {
//
//        try {
//            StatsUserDimension k = (StatsUserDimension) key;
//            OutputWritable v = (OutputWritable) value;
//
//            //获取活跃浏览器会员的值
//            int sessions =
//                    ((IntWritable) (v.getValue().get(new IntWritable(-1)))).get();
//
//            int sessionsLength = ((IntWritable) ((OutputWritable) value).
//                    getValue().get(new IntWritable(-2))).get();
//
//            int i = 0;
//            ps.setInt(++i, iDimension.getDimensionIdByObject(k.getStatsCommonDimension().getDateDimension()));
//            ps.setInt(++i, iDimension.getDimensionIdByObject(k.getStatsCommonDimension().getPlatformDimension()));
////            System.out.println(ps);
//
//            //修改1--浏览器会话   〈对于不同的指标，这列赋值都是不一样的〉
//            if (v.getKpi().equals(KpiType.BROWSER_SESSION)) {
//                ps.setInt(++i, iDimension.getDimensionIdByObject(k.getBrowserDimension()));
//            }
//
//            ps.setInt(++i, sessions);
//            ps.setInt(++i, sessionsLength);
//            ps.setString(++i, conf.get(GlobalConstants.RUNNING_DATE));//注意这里需要在runner类里面进行赋值
//            ps.setInt(++i, sessions);
//            ps.setInt(++i, sessionsLength);
//
//            ps.addBatch();//添加到批处理中，批量执行SQL语句
//        } catch (Exception e) {
//            logger.warn("给ps赋值失败！！！");
//        }
//    }
//}