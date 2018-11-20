package com.bigdata.analystic.mr.au;

import com.bigdata.Util.TimeUtil;
import com.bigdata.analystic.model.StatsUserDimension;
import com.bigdata.analystic.model.value.map.TimeOutputValue;
import com.bigdata.analystic.model.value.reduce.OutputWritable;
import com.bigdata.common.DateEnum;
import com.bigdata.common.KpiType;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @ClassName: ActiveUserReducer
 * @Description: TODO 活跃用户的reduce类
 * @Author: xqg
 * @Date: 2018/11/7 15:25
 */
public class ActiveUserReducer extends Reducer<StatsUserDimension, TimeOutputValue, StatsUserDimension, OutputWritable> {
    private static final Logger logger = Logger.getLogger(ActiveUserReducer.class);
    private OutputWritable v = new OutputWritable();
    private Set unique = new HashSet();//用于去重，利用HashSet
    private MapWritable map = new MapWritable();

    //*TODO ========================小时统计
    private Map<Integer,Set<String>> hourlyMap = new HashMap<Integer, Set<String>>();
    //小时存储到MapWritable
    private MapWritable houlyWritable = new MapWritable();

    //*初始化按小时的容器
    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        //初始化按小时的容器
        for(int i = 0; i < 24 ; i++){
            this.hourlyMap.put(i,new HashSet<String>());
            this.houlyWritable.put(new IntWritable(i),new IntWritable(0));
        }
    }

    @Override
    protected void reduce(StatsUserDimension key, Iterable<TimeOutputValue> values, Context context)
            throws IOException, InterruptedException {
        //清空map，因为map是在外面定义的，每一个key都需要调用一次reduce方法，也就是说上次操作会保留map中的key-value
        map.clear();

        try {
//            //循环
//            for (TimeOutputValue tv : values) {
//                //将uuid取出添加到set中进行去重操作
//                this.unique.add(tv.getId());
//
//                //*构建输出,活跃用户的小时数
//               //判断KpiType是否等于
//                if (key.getStatsCommonDimension().getKpiDimension().getKpiName().equals(KpiType.HOURLY_ACTIVE_USER.kpiName)) {
//                    //按小时的
//                    int hour = TimeUtil.getDateInfo(tv.getTime(), DateEnum.HOUR);//得到小时数
//                    this.hourlyMap.get(hour).add(tv.getId());
//                }
//            }
//
//            //*按小时统计活跃的用户
//            if(key.getStatsCommonDimension().getKpiDimension().getKpiName().equals(KpiType.HOURLY_ACTIVE_USER.kpiName)){
//                for (Map.Entry<Integer,Set<String>> en : hourlyMap.entrySet()){
//                    //构造输出的value
//                    this.houlyWritable.put(new IntWritable(en.getKey()),new IntWritable(en.getValue().size()));
//                }
//                this.v.setKpi(KpiType.HOURLY_ACTIVE_USER);
//                this.v.setValue(this.houlyWritable);//取出hour
//                context.write(key,this.v);
//            }
//
//            //构造输出的value
//            //根据kpi别名获取kpi类型（比较灵活） --- 第一种方法
//            this.v.setKpi(KpiType.valueOfKpiName(key.getStatsCommonDimension().getKpiDimension().getKpiName()));
//
//            //通过集合的size统计新增用户uuid的个数，前面的key可以随便设置，就是用来标识新增用户个数的（比较难理解）
//            this.map.put(new IntWritable(-1), new IntWritable(this.unique.size()));
//            this.v.setValue(this.map);
//            //输出
//            context.write(key, this.v);
            if (key.getStatsCommonDimension().getKpiDimension().getKpiName().equals(KpiType.HOURLY_ACTIVE_USER)){
                //循环
                for (TimeOutputValue tv : values) {
                    int hour = TimeUtil.getDateInfo(tv.getTime(), DateEnum.HOUR);
                    this.hourlyMap.get(hour).add(tv.getId());
                }

                //构造输出的value
                //根据kpi别名获取kpi类型（比较灵活） --- 第一种方法
                this.v.setKpi(KpiType.HOURLY_ACTIVE_USER);
                //循环
                for (Map.Entry<Integer,Set<String>> en : this.hourlyMap.entrySet()) {
                    this.houlyWritable.put(new IntWritable(en.getKey()), new IntWritable(en.getValue().size()));
                }
                this.v.setValue(houlyWritable);
                //输出
                context.write(key,this.v);
            }else {
                this.unique.clear();
                //循环
                for(TimeOutputValue tv : values){//循环
                    this.unique.add(tv.getId());//将uuid取出添加到set中进行去重操作
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            this.unique.clear();//清空操作
            this.hourlyMap.clear();
            this.houlyWritable.clear();
            for (int i = 0; i < 24; i++) {
                this.hourlyMap.put(i,new HashSet<String>());
                this.houlyWritable.put(new IntWritable(i),new IntWritable(0));
            }
        }

        /**
         * 注意点：
         * 如果只是输出到文件系统中，则不需要kpi，不需要声明集合map
         * value只需要uuid的个数，这就不要封装对象了
         */
    }
}

