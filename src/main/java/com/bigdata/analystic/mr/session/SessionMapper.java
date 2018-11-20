package com.bigdata.analystic.mr.session;

import com.bigdata.analystic.model.StatsCommonDimension;
import com.bigdata.analystic.model.StatsUserDimension;
import com.bigdata.analystic.model.base.BrowserDimension;
import com.bigdata.analystic.model.base.DateDimension;
import com.bigdata.analystic.model.base.KpiDimension;
import com.bigdata.analystic.model.base.PlatformDimension;
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
 * @ClassName: SessionMapper
 * @Description: TODO session的个数和时长
 * @Author: xqg
 * @Date: 2018/11/8 14:35
 * 会话分析主要同时计算会话个数和会话长度，主要应用在用户基本信息分析模块和浏览器信息分析模块这两部分。
 * 会话个数就是计算u_sd的唯一个数，长度就是每个会话的长度总和。
 *
 * 会话个数指的是计算所有u_sd的个数，会话长度就是计算每个会话的长度，然后计算这些长度的一个总值。
 * (注意：处理的数据为所有事件产生的数据)
 */
public class SessionMapper extends Mapper<LongWritable, Text, StatsUserDimension, TimeOutputValue> {
    private static final Logger logger = Logger.getLogger(SessionMapper.class);
    private StatsUserDimension k = new StatsUserDimension();
    private TimeOutputValue v = new TimeOutputValue();

    //用户会话
    private KpiDimension SessionKpi =
            new KpiDimension(KpiType.SESSION.kpiName);
    //浏览器会话
    private KpiDimension BrowserSessionKpi =
            new KpiDimension(KpiType.BROWSER_SESSION.kpiName);

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
//        if (StringUtils.isNotEmpty(en) && en.equals(EventEnum.PAGEVIEW.alias)) {
            //获取想要的字段
            String serverTime = fields[1];
            String platform = fields[13];//平台
            String sessionId = fields[5];//session
            String browserName = fields[24];//浏览器名字
            String browserVersion = fields[25];//浏览器版本

            if (StringUtils.isEmpty(serverTime) || StringUtils.isEmpty(sessionId)) {
                logger.info("serverTime & sessionId is null serverTime:" + serverTime + ".sessionId" + sessionId);
                return;
            }

            //构造输出的key
            long stime = Long.valueOf(serverTime);//时间
            PlatformDimension platformDimension =
                    PlatformDimension.getInstance(platform);//平台
            DateDimension dateDimension = DateDimension.buildDate(stime, DateEnum.DAY);
            StatsCommonDimension statsCommonDimension = this.k.getStatsCommonDimension();
            //为StatsCommonDimension设值
            statsCommonDimension.setDateDimension(dateDimension);
            statsCommonDimension.setPlatformDimension(platformDimension);

            //用户模块会话
            //设置默认的浏览器对象(因为活跃用户指标并不需要浏览器维度，所以赋值为空)
            BrowserDimension defaultBrowserDimension = new BrowserDimension("", "");
            statsCommonDimension.setKpiDimension(SessionKpi);
            this.k.setBrowserDimension(defaultBrowserDimension);
            this.k.setStatsCommonDimension(statsCommonDimension);
            this.v.setId(sessionId);

            //一定要设置
            this.v.setTime(stime);
            context.write(this.k, this.v);//输出

            //浏览器模块会话
            statsCommonDimension.setKpiDimension(BrowserSessionKpi);
            BrowserDimension browserDimension = new BrowserDimension(browserName, browserVersion);
            this.k.setBrowserDimension(browserDimension);
            this.k.setStatsCommonDimension(statsCommonDimension);

            //一定要设置
            this.v.setTime(stime);
            context.write(this.k, this.v);//输出
//        }
    }
}