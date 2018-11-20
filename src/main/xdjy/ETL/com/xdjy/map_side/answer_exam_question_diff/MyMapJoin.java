/**
 * Copyright (C), 2015-2018, XXX 有限公司
 * FileName: MyMap
 * Author: Administrator
 * Date: 2018-11-12 下午 3:45
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
package ETL.com.xdjy.map_side.answer_exam_question_diff;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.partition.HashPartitioner;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * 第三步:
 */
public class MyMapJoin {
    // 定义输入路径
    private static String INPUT_PATH1 = "hdfs://192.168.198.60:9000/xdjy/result/answer_exam_question/answer_exam_question.log";
    //加载到内存的表的路径
    private static String INPUT_PATH2 = "hdfs://192.168.198.60:9000/xdjy/question_difficulty/question_difficulty.log";
    // 定义输出路径
    private static String OUT_PATH = "hdfs://192.168.198.60:9000/xdjy/result/answer_exam_question_diff";

    public static void main(String[] args) {
        try {
            // 创建配置信息
            Configuration conf = new Configuration();
//            获取命令行的参数
//            String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
//            当参数违法时，中断程序
//            if (otherArgs.length != 3) {
//                System.err.println("Usage:MyMapJoin<in1> <in2> <out>");
//                System.exit(1);
//            }
//            给路径赋值
//            INPUT_PATH1 = otherArgs[0];
//            INPUT_PATH2 = otherArgs[1];
//            OUT_PATH = otherArgs[2];
            //创建文件系统
            FileSystem fileSystem = FileSystem.get(new URI(OUT_PATH), conf);
// 如果输出目录存在，我们就删除
            if (fileSystem.exists(new Path(OUT_PATH))) {
                fileSystem.delete(new Path(OUT_PATH), true);
            }
            //添加到内存中的文件(随便添加多少个文件)
            DistributedCache.addCacheFile(new Path(INPUT_PATH2).toUri(), conf);
            // 创建任务
            Job job = new Job(conf, MyMapJoin.class.getName());
            // 打成jar包运行，这句话是关键
            job.setJarByClass(MyMapJoin.class);
            //1.1 设置输入目录和设置输入数据格式化的类
            FileInputFormat.setInputPaths(job, INPUT_PATH1);
            job.setInputFormatClass(TextInputFormat.class);
            //1.2 设置自定义Mapper类和设置map函数输出数据的key和value的类型
            job.setMapperClass(MapJoinMapper.class);
            job.setMapOutputKeyClass(Text.class);
            job.setMapOutputValueClass(NullWritable.class);
            //1.3 设置分区和reduce数量
            job.setPartitionerClass(HashPartitioner.class);
            job.setNumReduceTasks(0);
            FileOutputFormat.setOutputPath(job, new Path(OUT_PATH));
            // 提交作业 退出
            System.exit(job.waitForCompletion(true) ? 0 : 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class MapJoinMapper extends Mapper<LongWritable, Text, Text, NullWritable> {
        private Text k = new Text();
        private Map<Integer, String> joinData = new HashMap<Integer, String>();

        @Override
        protected void setup(Context context) throws IOException, InterruptedException {
// 预处理把要关联的文件加载到缓存中
            Path[] paths = DistributedCache.getLocalCacheFiles(context.getConfiguration());
            System.out.println(paths[0]);
// 我们这里只缓存了一个文件，所以取第一个即可，创建BufferReader去读取
            BufferedReader reader = new BufferedReader(new FileReader(paths[0].toString()));
            String str = null;
            try {
// 一行一行读取
                while ((str = reader.readLine()) != null) {
// 对缓存中的表进行分割
                    String[] splits = str.split("\\+");

// 把字符数组中有用的数据存在一个Map中
                    String m_res = splits[1];
                    joinData.put(Integer.parseInt(splits[0]), m_res);
//                    System.out.println(str);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                reader.close();
            }
        }

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException,
                InterruptedException {
// 获取从HDFS中加载的表
            String[] values = value.toString().split("\\+");
//
// 获取关联字段depNo，这个字段是关键
            int question_diff_id = Integer.parseInt(values[14]);

// 根据depNo从内存中的关联表中获取要关联的属性depName
            String res = joinData.get(question_diff_id);

            if (res != null && StringUtils.isNotEmpty(res)) {
                // 写出去
                k.set(value.toString() + "+" + res);
                System.out.println(k);
                context.write(k, NullWritable.get());
            }
        }
    }
}