package com.bigdata.analystic.mr.local;

import com.bigdata.analystic.model.StatsLocationDimension;
import com.bigdata.analystic.model.StatsUserDimension;
import com.bigdata.analystic.model.value.map.LocationOutputValue;
import com.bigdata.analystic.model.value.map.TimeOutputValue;
import com.bigdata.analystic.model.value.reduce.LocationReduceOutput;
import com.bigdata.common.KpiType;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @ClassName: LocalReducer
 * @Description: TODO 地域的reduce类
 * @Author: xqg
 * @Date: 2018/11/8 15:25
 */
public class LocalReducer
        extends Reducer<StatsLocationDimension, LocationOutputValue, StatsLocationDimension, LocationReduceOutput> {
    private static final Logger logger = Logger.getLogger(LocalReducer.class);
    private StatsLocationDimension k = new StatsLocationDimension();
    private LocationReduceOutput v = new LocationReduceOutput();
    //用于去重，利用HashSet
    private Set<String> unique = new HashSet<String>();
    private Map<String, Integer> map = new HashMap<String, Integer>();

    @Override
    protected void reduce(StatsLocationDimension key, Iterable<LocationOutputValue> values,
                          Context context) throws IOException, InterruptedException {
        //清空set
        this.unique.clear();
        //循环
        for (LocationOutputValue tv : values) {
            if (StringUtils.isNotEmpty(tv.getUid().trim())) {
                //循环将uuid添加到set中
                this.unique.add(tv.getUid());
            }

            if (StringUtils.isNotEmpty(tv.getSid().trim())) {
                if (map.containsKey(tv.getSid())) {
                    //不是跳出会话个数
                    this.map.put(tv.getSid(), 2);
                } else {
                    //跳出会话个数
                    this.map.put(tv.getSid(), 1);
                }
            }
        }
        //构造输出value
        this.v.setAus(this.unique.size());
        this.v.setSession(this.map.size());
        int bounceSession = 0;
        for (Map.Entry<String,Integer> en:map.entrySet()){
            if(en.getValue() == 1){
                bounceSession ++;
            }
        }
        this.v.setBounce_session(bounceSession);
        //设置kpi
        this.v.setKpi(KpiType.valueOfKpiName(key.getStatsCommonDimension().getKpiDimension().getKpiName()));
        //输出
        context.write(key,this.v);

    }
}

