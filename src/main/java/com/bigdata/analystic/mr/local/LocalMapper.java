package com.bigdata.analystic.mr.local;

import com.bigdata.analystic.model.StatsCommonDimension;
import com.bigdata.analystic.model.StatsLocationDimension;
import com.bigdata.analystic.model.StatsUserDimension;
import com.bigdata.analystic.model.base.*;
import com.bigdata.analystic.model.value.map.LocationOutputValue;
import com.bigdata.analystic.model.value.map.TimeOutputValue;
import com.bigdata.common.Constants.EventEnum;
import com.bigdata.common.DateEnum;
import com.bigdata.common.KpiType;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.log4j.Logger;

import java.io.IOException;

/**
 * @ClassName: LocalMapper
 * @Description: TODO 地域模块
 * @Author: xqg
 * @Date: 2018/11/8 14:35
 */
public class LocalMapper extends Mapper<LongWritable, Text, StatsLocationDimension,
        LocationOutputValue> {
    private static final Logger logger = Logger.getLogger(LocalMapper.class);
    private StatsLocationDimension k = new StatsLocationDimension();
    private LocationOutputValue v = new LocationOutputValue();
    //地域Kpi
    private KpiDimension localKpi = new KpiDimension(KpiType.LOCAL.kpiName);

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String line = value.toString();
        if (StringUtils.isEmpty(line)) {
            return;
        }

        //拆分
        String[] fields = line.split("\u0001");
        //pv是事件名称---pageview事件
        String en = fields[2];
        if (StringUtils.isNotEmpty(en) && en.equals(EventEnum.PAGEVIEW.alias)) {
            //获取想要的字段
            String serverTime = fields[1];
            String platform = fields[13];//平台
            String uuid = fields[3];//uuid
            String sid = fields[5];//sid
            String country = fields[28];//国家
            String province = fields[29];//省
            String city = fields[30];//市

            if (StringUtils.isEmpty(serverTime)) {
                logger.info("serverTime & memberId is null serverTime:" + serverTime +
                        ".uuid" + uuid);
                return;
            }

            //构造输出的key
            long stime = Long.valueOf(serverTime);//时间
            PlatformDimension platformDimension = PlatformDimension.getInstance(platform);//平台
            DateDimension dateDimension = DateDimension.buildDate(stime, DateEnum.DAY);
            //地域基础类
            LocationDimension locationDimension = LocationDimension.getInstance(country, province, city);
            StatsCommonDimension statsCommonDimension = this.k.getStatsCommonDimension();

            //为StatsCommonDimension设值,公共维度,时间,平台维度
            statsCommonDimension.setDateDimension(dateDimension);
            statsCommonDimension.setPlatformDimension(platformDimension);

            //地域模块
            statsCommonDimension.setKpiDimension(localKpi);
            this.k.setLocationDimension(locationDimension);
            this.k.setStatsCommonDimension(statsCommonDimension);
            this.v.setUid(uuid);
            this.v.setSid(sid);
            context.write(this.k, this.v);//输出
        }
    }
}