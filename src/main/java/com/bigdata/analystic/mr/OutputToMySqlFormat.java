package com.bigdata.analystic.mr;

import com.bigdata.Util.JdbcUtil;
import com.bigdata.analystic.model.StatsBaseDimension;
import com.bigdata.analystic.model.value.reduce.OutputWritable;
import com.bigdata.analystic.mr.service.IDimension;
import com.bigdata.analystic.mr.service.implement.IDimensionImpl;
import com.bigdata.common.KpiType;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.output.FileOutputCommitter;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * 将结果输出到mysql的自定义类
 *
 * @ClassName: OutputToMySqlFormat
 * @Description: TODO
 * @Author: xqg
 * @Date: 2018/11/5 14:34
 */
public class OutputToMySqlFormat extends OutputFormat<StatsBaseDimension, OutputWritable> {

    //DBOutputFormat

    /**
     * 获取输出记录
     *
     * @param taskAttemptContext
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    @Override
    public RecordWriter<StatsBaseDimension, OutputWritable> getRecordWriter(TaskAttemptContext taskAttemptContext) throws IOException, InterruptedException {
        //获取连接
        Connection conn = JdbcUtil.getConn();
        //获取SQL
        Configuration conf = taskAttemptContext.getConfiguration();
        //获取纬度ID
        IDimension iDimension = new IDimensionImpl();
        return new OutputToMysqlRecordWriter(conf, conn, iDimension);
    }

    @Override
    public void checkOutputSpecs(JobContext jobContext) throws IOException, InterruptedException {
        //检测输出路径
    }

    @Override
    public OutputCommitter getOutputCommitter(TaskAttemptContext taskAttemptContext) throws IOException, InterruptedException {
        return new FileOutputCommitter(null, taskAttemptContext);
    }

    /**
     * 用于封装写出记录到mysql的信息
     */
    public static class OutputToMysqlRecordWriter extends RecordWriter<StatsBaseDimension, OutputWritable> {
        //获取输出sql语句
        Configuration conf = null;
        Connection conn = null;
        IDimension iDimension = null;
        //存储kpi-ps
        private Map<KpiType, PreparedStatement> map = new HashMap<KpiType, PreparedStatement>();
        //存储kpi-对应的输出sql
        private Map<KpiType, Integer> batch = new HashMap<KpiType, Integer>();

        public OutputToMysqlRecordWriter(Configuration conf, Connection conn, IDimension iDimension) {
            this.conf = conf;
            this.conn = conn;
            this.iDimension = iDimension;
        }

        /**
         * 真正的写出方法
         *
         * @param key
         * @param value
         * @throws IOException
         * @throws InterruptedException
         */
        @Override
        public void write(StatsBaseDimension key, OutputWritable value) throws IOException, InterruptedException {
            //获取kpi
            KpiType kpi = value.getKpi();
            PreparedStatement ps = null;
            try {

                //获取ps
                if (map.containsKey(kpi)) {
                    ps = map.get(kpi);
                } else {
                    ps = conn.prepareStatement(conf.get(kpi.kpiName));
                    map.put(kpi, ps);  //将新增加的ps存储到map中
                }
                int count = 1;
                this.batch.put(kpi, count);
                count++;

                //为ps赋值准备
                String className = conf.get("writer_" + kpi.kpiName);
                System.out.println(kpi.kpiName);
               System.out.println(className);

                //com.bigdata.analystic.mr.nu.NewUserOutputWriter
                //TODO 反射
                Class<?> classz = Class.forName(className); //将包名+类名转换成类
                IOutputWriter writer = (IOutputWriter) classz.newInstance();
                //调用IOutputWriter中的output方法
                writer.output(conf, key, value, ps, iDimension);

                //对赋值好的ps进行执行t
                if (batch.size() % 50 == 0 || batch.get(kpi) % 50 == 0) {  //有50个ps执行
                    ps.executeBatch();  //批量执行
                    this.conn.commit(); //提交批处理执行
                    batch.remove(kpi); //将执行完的ps移除掉
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * @param taskAttemptContext
         * @throws IOException
         * @throws InterruptedException
         */
        @Override
        public void close(TaskAttemptContext taskAttemptContext) throws IOException, InterruptedException {
            try {
                for (Map.Entry<KpiType, PreparedStatement> en : map.entrySet()) {
                    en.getValue().executeBatch(); //将剩余的ps进行执行
//                    this.conn.commit();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                for (Map.Entry<KpiType, PreparedStatement> en : map.entrySet()) {
                    JdbcUtil.close(conn, en.getValue(), null); //关闭所有能关闭的资源
                }
            }
        }
    }
}
