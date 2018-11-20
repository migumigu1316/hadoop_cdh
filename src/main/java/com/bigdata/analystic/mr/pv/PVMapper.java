package com.bigdata.analystic.mr.pv;

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
 * @ClassName: PVMapper
 * @Description: TODO PV
 * @Author: xqg
 * @Date: 2018/11/8 14:35
 *  pv的计算其实就是计算访问url的次数，不涉及的去重，也就是说一个用户访问一个url多少次就算多少pv值。
 *  也就是说最终结果是一个pageview事件产生一个pv值，不涉及到任何去重操作。
 */
public class PVMapper extends Mapper<LongWritable, Text, StatsUserDimension, TimeOutputValue> {
    private static final Logger logger = Logger.getLogger(PVMapper.class);
    private StatsUserDimension k = new StatsUserDimension();
    private TimeOutputValue v = new TimeOutputValue();

    //用户pv
    private KpiDimension pvKpi = new KpiDimension(KpiType.PAGEVIEW.kpiName);
    //浏览器pv
    private KpiDimension BrowserPvKpi = new KpiDimension(KpiType.BROWSER_PAGEVIEW.kpiName);

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

            //获取想要的字段
            String serverTime = fields[1];
            String platform = fields[13];//平台
            String pv = fields[10];//pv的url
            String browserName = fields[24];//浏览器名字
            String browserVersion = fields[25];//浏览器版本

            if (StringUtils.isEmpty(serverTime) || StringUtils.isEmpty(pv)) {
                logger.info("serverTime & pv is null serverTime:" + serverTime + ".pv" + pv);
                return;
            }

            //构造输出的key
            long stime = Long.valueOf(serverTime);//时间
            PlatformDimension platformDimension = PlatformDimension.getInstance(platform);//平台
            DateDimension dateDimension = DateDimension.buildDate(stime, DateEnum.DAY);
            StatsCommonDimension statsCommonDimension = this.k.getStatsCommonDimension();
            //为StatsCommonDimension设值
            statsCommonDimension.setDateDimension(dateDimension);
            statsCommonDimension.setPlatformDimension(platformDimension);

//            //用户pv
//            //设置默认的浏览器对象(因为活跃用户指标并不需要浏览器维度，所以赋值为空)
//            BrowserDimension defaultBrowserDimension = new BrowserDimension("", "");
//            statsCommonDimension.setKpiDimension(pvKpi);
//            this.k.setBrowserDimension(defaultBrowserDimension);
//            this.k.setStatsCommonDimension(statsCommonDimension);
//            this.v.setId(pv);
//            context.write(this.k, this.v);//输出

            //浏览器pv
            statsCommonDimension.setKpiDimension(BrowserPvKpi);
            BrowserDimension browserDimension = new BrowserDimension(browserName, browserVersion);
            this.k.setBrowserDimension(browserDimension);
            this.k.setStatsCommonDimension(statsCommonDimension);
            context.write(this.k, this.v);//输出

    }
}