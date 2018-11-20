package com.hadoopCDH.etl.mr;

import com.hadoopCDH.etl.IpUtil;
import com.hadoopCDH.etl.ip.TimeUtil;
import com.hadoopCDH.etl.ip.UserAgentUtil;
import jodd.util.URLDecoder;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;
/**
 * 192.168.198.1^A1541076976.836^A192.168.198.60^A/?en=e_pv&p_url=http%3A%2F%2Flocalhost%3A8080%2Fdemo3.jsp&p_ref=http%3A%2F%2Flocalhost%3A8080%2Fdemo4.jsp&tt=%E6%B5%8B%E8%AF%95%E9%A1%B5%E9%9D%A23&ver=1&pl=website&sdk=js&u_ud=D05607D5-49DE-4390-8207-08B9E19E76DF&u_mid=liyadong&u_sd=DC966632-75C0-46A7-B882-27C023A401A6&c_time=1541084536964&l=zh-CN&b_iev=Mozilla%2F5.0%20(Windows%20NT%2010.0%3B%20Win64%3B%20x64)%20AppleWebKit%2F537.36%20(KHTML%2C%20like%20Gecko)%20Chrome%2F71.0.3554.0%20Safari%2F537.36&b_rst=1366*768
 */

public class QingXiShuJu {
    static class MyMapper extends Mapper<LongWritable, Text, Text, Text> {
        @Override
        protected void map(LongWritable key, Text value, Context context)
                throws IOException, InterruptedException {
            String lines = value.toString();
            System.out.println(lines);//每次传入的数据

            //数据格式用^A连接,判断传入的每条数据是否包含^A
            if (lines.contains("^A")) {
                String[] split = lines.split("\\^A");
                System.out.println(split[1]);

                //调用封装好的IPUtils工具类解析ip
                IpUtil.RegionInfo ip = IpUtil.getRegionInfoByIp(split[0]);
                String times = TimeUtil.times(split[1]);
                System.out.println(times);

                //正则匹配
                String url = split[3].replaceAll("%(?![0-9a-fA-F]{2})", "%25");
                //
                String urlStr = URLDecoder.decode(url, "UTF-8");
                //调用封装好的UserAgentUtil工具类,进行解析浏览器相关信息
                UserAgentUtil.UserAgentInfo userAgentInfo = UserAgentUtil.analyticUserAgent(urlStr);
                context.write(new Text(ip + "\t" + times + "\t" + userAgentInfo),
                        new Text(ip + "\t" + times + "\t" + urlStr));
            }
        }
    }

    static class MyReduce extends Reducer<Text, Text, Text, NullWritable> {
        @Override
        protected void reduce(Text key, Iterable<Text> values, Context context)
                throws IOException, InterruptedException {
            for (Text value : values) {
                value = null;
            }
            context.write(key, NullWritable.get());
        }
    }

    public static void main(String[] args) throws Exception {
        //1、获取配置对象Configuration
        Configuration conf = new Configuration();

        //2、设置hdfs的连接参数
        conf.set("fs.defaultFs", "hdfs://hadoop01:9000");

        //3.创建一个job对象,创建一个实例化的
        Job job = Job.getInstance(conf, "qingxishuju");

        //4.创建job的执行路径
        job.setJarByClass(QingXiShuJu.class);

        //5.设置job执行的Mapper任务的业务类
        job.setMapperClass(MyMapper.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Text.class);

        //6.设置reduce端的相关参数
        job.setReducerClass(MyReduce.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(NullWritable.class);

        //7.输入路径
        FileInputFormat.setInputPaths(job, new Path(
                "hdfs://hadoop01:9000/flume/events/2018-11-01"));
        //8.输出路径
        FileOutputFormat.setOutputPath(job, new Path(
                "hdfs://hadoop01:9000//flume_out/oo"));
        /**
         * RegionInfo{country='中国', province='北京市', city='昌平区'}    2018-11-01 20:55:43     Chrome  71.0.3554.0     Windows Windows
         * RegionInfo{country='中国', province='北京市', city='昌平区'}    2018-11-01 20:55:47     Chrome  71.0.3554.0     Windows Windows
         * RegionInfo{country='中国', province='北京市', city='昌平区'}    2018-11-01 20:55:48     Chrome  71.0.3554.0     Windows Windows
         * RegionInfo{country='中国', province='北京市', city='昌平区'}    2018-11-01 20:55:50     Chrome  71.0.3554.0     Windows Windows
         */
        //9.提交job到集群中
        boolean b = job.waitForCompletion(true);

        System.exit(b ? 0 : 1);
    }
}
