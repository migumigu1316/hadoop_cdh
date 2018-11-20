//package jsonAnalysis;
//
//import org.apache.hadoop.conf.Configuration;
//import org.apache.hadoop.fs.Path;
//import org.apache.hadoop.io.LongWritable;
//import org.apache.hadoop.io.NullWritable;
//import org.apache.hadoop.io.Text;
//import org.apache.hadoop.mapreduce.Job;
//import org.apache.hadoop.mapreduce.Mapper;
//import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
//import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
//import org.codehaus.jettison.json.JSONException;
//import org.codehaus.jettison.json.JSONObject;
//
//import java.io.IOException;
//import java.util.Iterator;
//
///**
// * @ClassName: jsonMR_google
// * @Description: TODO
// * @Author: xqg
// * @Date: 2018/11/12 16:08
// */
//public class jsonMR_google {
//    static class JsonMapper extends Mapper<LongWritable, Text, Text, NullWritable> {
//        private static Text k = new Text();
//        @Override
//        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
//            String lines = value.toString();
//            String[] split = lines.split("\t");
//            try {
//                JSONObject jsonObject = new JSONObject(split[16]);
//                Iterator keys = jsonObject.keys();
//                while (keys.hasNext()) {
//                    String string = keys.next().toString();
//                    String useranswer = jsonObject.getJSONObject(string).getString("useranswer");
//                    String score = jsonObject.getJSONObject(string).getString("score");
//                    k.set(string + "\t" + useranswer + "\t" + score);
//                    context.write(k, NullWritable.get());
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//        }
//    }
//
//    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
//        Configuration conf = new Configuration();
//        conf.set("fs.defaultFS","file:///");
//        conf.set("mapreduce.framework.name","local");
//        Job job = Job.getInstance(conf, "JsonDemo");
//        job.setJarByClass(JsonDemo.class);
//        job.setMapperClass(JsonMapper.class);
//        job.setMapOutputKeyClass(Text.class);
//        job.setMapOutputValueClass(NullWritable.class);
//        job.setOutputKeyClass(Text.class);
//        job.setOutputValueClass(NullWritable.class);
//        //9、设置job的输入文件的路径
//        FileInputFormat.setInputPaths(job, new Path("E:\\test\\"));
//        //10、设置job的输出文件的路径
//        FileOutputFormat.setOutputPath(job, new Path("E:\\gp1809\\data\\a"));
//
//        //11、提交job到集群中
//        boolean b = job.waitForCompletion(true);
//        System.exit(b ? 0 : 1);
//    }
//}
//}
