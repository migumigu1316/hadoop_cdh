package jsonAnalysis;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Iterator;


public class JsonMR {

    static class JsonMapper extends Mapper<LongWritable, Text, Text, NullWritable> {
        private static Text k = new Text();


        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String lines = value.toString();
            String[] split = lines.split("\\+");
            String values;

            try {
                JSONObject json = new JSONObject(split[16]);
                Iterator keys = json.keys();
                while (keys.hasNext()) {
                    String itemId = (String) keys.next();
                    String v = json.getString(itemId);
                    JSONObject json1 = new JSONObject(v);
                    String score = json1.getString("score");
                    values = split[0] + "+" + split[1] + "+" + split[2] + "+" + split[4] + "+" + split[5] + "+" + split[7] + "+" + split[8] + "+" + split[9] + "+" + split[11] + "+" + itemId + "+" + score;
                    System.out.println(values);
                    k.set(values);
                    context.write(k, NullWritable.get());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        Configuration conf = new Configuration();
//        conf.set("fs.defaultFS", "file:///");
//        conf.set("mapreduce.framework.name", "local");
        conf.set("fs.defaultFS", "hdfs://192.168.198.60:9000");
        Job job = Job.getInstance(conf, "JsonMR");

        job.setJarByClass(JsonMR.class);

        job.setMapperClass(JsonMapper.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(NullWritable.class);
        // job.setOutputKeyClass(Text.class);
        // job.setOutputValueClass(NullWritable.class);
        //9、设置job的输入文件的路径
        FileInputFormat.setInputPaths(job, new Path("hdfs://192.168.198.60:9000/xdjy/answer_paper/"));
        //10、设置job的输出文件的路径
        FileOutputFormat.setOutputPath(job, new Path("hdfs://192.168.198.60:9000/xdjy/json_result"));

        //11、提交job到集群中
        boolean b = job.waitForCompletion(true);
        System.exit(b ? 0 : 1);
    }
}
