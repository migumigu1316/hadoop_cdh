package com.bigdata.analystic.mr.session;

import com.bigdata.analystic.model.StatsUserDimension;
import com.bigdata.analystic.model.value.map.TimeOutputValue;
import com.bigdata.analystic.model.value.reduce.OutputWritable;
import com.bigdata.common.GlobalConstants;
import com.bigdata.common.KpiType;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.*;

/**
 * @ClassName: SessionReducer
 * @Description: TODO
 * @Author: xqg
 * @Date: 2018/11/8 15:25
 */
public class SessionReducer
        extends Reducer<StatsUserDimension, TimeOutputValue, StatsUserDimension, OutputWritable> {
    private static final Logger logger = Logger.getLogger(SessionReducer.class);
    private OutputWritable v = new OutputWritable();
//    private Set unique = new HashSet();//用于去重，利用HashSet
    private Map<String, List<Long>> map = new HashMap<String, List<Long>>();

    @Override
    protected void reduce(StatsUserDimension key, Iterable<TimeOutputValue> values, Context context)
            throws IOException, InterruptedException {
        //清空map，因为map是在外面定义的，每一个key都需要调用一次reduce方法，也就是说上次操作会保留map中的key-value
        this.map.clear();

        //循环map阶段传过来的value
        for (TimeOutputValue tv : values) {
            String sessionId = tv.getId();
            long serverTime = tv.getTime();
            //存储时间
            if (map.containsKey(tv.getId())) {
                List<Long> li = map.get(sessionId);
                li.add(serverTime);
                map.put(sessionId, li);
            } else {
                List<Long> li = new ArrayList<Long>();
                li.add(serverTime);
                map.put(sessionId, li);
            }
        }

        //根据kpi别名获取kpi类型（比较灵活） --- 第一种方法
        //构造输出的value
        MapWritable mapWritable = new MapWritable();
        mapWritable.put(new IntWritable(-1), new IntWritable(this.map.size()));

        //session的时长
        int sesssionLength = 0;
        for (Map.Entry<String, List<Long>> en : map.entrySet()) {
            if (en.getValue().size() >= 2) {
                Collections.sort(en.getValue());
                sesssionLength += (en.getValue().get(en.getValue().size() - 1) - en.getValue().get(0));
            }
        }

        if (sesssionLength > 0 && sesssionLength <= GlobalConstants.DAY_OF_MILLSECOND) {
            //不足一秒算一秒
            if (sesssionLength % 1000 == 0) {
                sesssionLength = sesssionLength / 1000;
            } else {
                sesssionLength = sesssionLength / 1000 + 1;
            }
        }
        mapWritable.put(new IntWritable(-2), new IntWritable(sesssionLength));
        this.v.setValue(mapWritable);
        this.v.setKpi(KpiType.valueOfKpiName(key.getStatsCommonDimension().getKpiDimension().getKpiName()));
        //输出即可
        context.write(key, this.v);
    }

}

