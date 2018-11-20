package com.bigdata.analystic.mr.pv;

import com.bigdata.analystic.model.StatsUserDimension;
import com.bigdata.analystic.model.value.map.TimeOutputValue;
import com.bigdata.analystic.model.value.reduce.OutputWritable;
import com.bigdata.common.KpiType;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @ClassName: PVReducer
 * @Description: TODO PV的reduce类
 * @Author: xqg
 * @Date: 2018/11/8 15:25
 */
public class PVReducer extends Reducer<StatsUserDimension, TimeOutputValue, StatsUserDimension, OutputWritable> {
    private static final Logger logger = Logger.getLogger(PVReducer.class);
    private OutputWritable v = new OutputWritable();
    //url的统计,pv是不去重的,url有几次,就计算几次
    private List<String> urlList = new ArrayList<>();
    private MapWritable map = new MapWritable();

    @Override
    protected void reduce(StatsUserDimension key, Iterable<TimeOutputValue> values, Context context)
            throws IOException, InterruptedException {
        map.clear();//清空map，因为map是在外面定义的，每一个key都需要调用一次reduce方法，也就是说上次操作会保留map中的key-value

        for (TimeOutputValue tv : values) {//循环
            //将url添加到urlList中
            this.urlList.add(tv.getId());
        }

        //构造输出的value
        //根据kpi别名获取kpi类型（比较灵活） --- 第一种方法
        this.v.setKpi(KpiType.valueOfKpiName(key.getStatsCommonDimension().getKpiDimension().getKpiName()));

        //这样写比较死，对于每一个kpi都需要进行判断
//        if(key.getStatsCommonDimension().getKpiDimension().getKpiName().equals(KpiType.NEW_USER.kpiName)){
//            this.v.setKpi(KpiType.NEW_USER);
//        }

        this.map.put(new IntWritable(-1), new IntWritable(this.urlList.size()));
        this.v.setValue(this.map);
        //输出
        context.write(key, this.v);
        this.urlList.clear();//清空操作
    }
}

