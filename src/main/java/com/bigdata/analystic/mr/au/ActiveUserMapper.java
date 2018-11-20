package com.bigdata.analystic.mr.au;

import com.bigdata.analystic.model.StatsCommonDimension;
import com.bigdata.analystic.model.StatsUserDimension;
import com.bigdata.analystic.model.base.BrowserDimension;
import com.bigdata.analystic.model.base.DateDimension;
import com.bigdata.analystic.model.base.KpiDimension;
import com.bigdata.analystic.model.base.PlatformDimension;
import com.bigdata.analystic.model.value.map.TimeOutputValue;
import com.bigdata.common.Constants;
import com.bigdata.common.DateEnum;
import com.bigdata.common.KpiType;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.log4j.Logger;

import java.io.IOException;

/**
 * @ClassName: ActiveUserMapper
 * @Description: TODO 活跃的用户,所有事件中uuid去重个数
 * @Author: xqg
 * @Date: 2018/11/7 14:35
 * active_user计算规则：当天所有数据中，uuid的去重个数。
 * * 最终数据保存：stats_user和stats_device_browser。涉及到的列(除了维度列和created列外)：
 *   active_users。涉及到其他表有dimension_platform、dimension_date、dimension_browser。
 */
public class ActiveUserMapper extends Mapper<LongWritable, Text, StatsUserDimension, TimeOutputValue> {
    private static final Logger logger = Logger.getLogger(ActiveUserMapper.class);
    private StatsUserDimension k = new StatsUserDimension();
    private TimeOutputValue v = new TimeOutputValue();

    //活跃用户
    private KpiDimension activeUserKpi = new KpiDimension(KpiType.ACTIVE_USER.kpiName);
    //活跃浏览器用户
    private KpiDimension activeBrowserUserKpi = new KpiDimension(KpiType.BROWSER_ACTIVE_USER.kpiName);
    //TODO 添加统计hourly active user的代码。
    private KpiDimension hourlyActiveUserKpi = new KpiDimension(KpiType.HOURLY_ACTIVE_USER.kpiName);

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String line = value.toString();
        if (StringUtils.isEmpty(line)) {
            return;
        }

        //拆分
        String[] fields = line.split("\u0001");
        //en是事件名称
        String en = fields[2];

            //获取想要的字段
            String serverTime = fields[1];
            String platform = fields[13];//平台
            String uuid = fields[3];//uuid
            String browserName = fields[24];//浏览器名字
            String browserVersion = fields[25];//浏览器版本

            if (StringUtils.isEmpty(serverTime) || StringUtils.isEmpty(uuid)) {
                logger.info("serverTime & uuid is null serverTime:" + serverTime + ".uuid" + uuid);
                return;
            }

            //构造输出的key
            long stime = Long.valueOf(serverTime);//时间
            //TODO 一定要设置,时间
            this.v.setTime(stime);
            PlatformDimension platformDimension = PlatformDimension.getInstance(platform);//平台
            DateDimension dateDimension = DateDimension.buildDate(stime, DateEnum.DAY);
            StatsCommonDimension statsCommonDimension = this.k.getStatsCommonDimension();
            //为StatsCommonDimension设值
            statsCommonDimension.setDateDimension(dateDimension);
            statsCommonDimension.setPlatformDimension(platformDimension);

            //用户模块活跃用户
            //设置默认的浏览器对象(因为活跃用户指标并不需要浏览器维度，所以赋值为空)
            BrowserDimension defaultBrowserDimension = new BrowserDimension("", "");
            statsCommonDimension.setKpiDimension(activeUserKpi);
            this.k.setBrowserDimension(defaultBrowserDimension);
            this.k.setStatsCommonDimension(statsCommonDimension);
            this.v.setId(uuid);
            //统计用户的输出
            context.write(this.k, this.v);

            //TODO 输出小时统计的数据
            //不要写在浏览器的下面,和浏览器没关系
            statsCommonDimension.setKpiDimension(hourlyActiveUserKpi);
            //这里必须设置,不然reduce端用不了
            this.k.setStatsCommonDimension(statsCommonDimension);
            //统计小时的输出
            context.write(this.k, this.v);

            //浏览器模块活跃用户
            statsCommonDimension.setKpiDimension(activeBrowserUserKpi);
            BrowserDimension browserDimension = new BrowserDimension(browserName, browserVersion);
            this.k.setBrowserDimension(browserDimension);
            this.k.setStatsCommonDimension(statsCommonDimension);
            //统计浏览器的输出
            context.write(this.k, this.v);

    }
}