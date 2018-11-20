package com.bigdata.analystic.mr.am;

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
 * @ClassName: ActiveMemberMapper
 * @Description: TODO 活跃会员,所有事件中 memberID去重个数
 * @Author: xqg
 * @Date: 2018/11/8 14:35
 * 活跃会员(active_member)计算规则：计算当天(确定时间维度信息)的pageview事件的数据中memberid的
 * 去重个数。(这里只所以选择pageview事件，是可能会存在一种可能：某个会员在当天没有进行任何操作，
 * 但是他订单支付成功的操作在今天在被触发，这样在所有数据中就会出现一个java_server平台产生的订单
 * 支付成功事件，包含会员id)。
 * * 最终数据保存：stats_user和stats_device_browser。涉及到的列(除了维度列和created列外)：
 *   active_members。涉及到其他表有dimension_platform、dimension_date、dimension_browser。
 */
public class ActiveMemberMapper extends Mapper<LongWritable, Text, StatsUserDimension, TimeOutputValue> {
    private static final Logger logger = Logger.getLogger(ActiveMemberMapper.class);
    private StatsUserDimension k = new StatsUserDimension();
    private TimeOutputValue v = new TimeOutputValue();

    //活跃会员
    private KpiDimension activeMemberKpi =
            new KpiDimension(KpiType.ACTIVE_MEMBER.kpiName);
    //活跃浏览器会员
    private KpiDimension activeBrowserMemberKpi =
            new KpiDimension(KpiType.BROWSER_ACTIVE_MEMBER.kpiName);

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
//            String uuid = fields[3];//uuid
            String memberId = fields[4];//memberId
            String browserName = fields[24];//浏览器名字
            String browserVersion = fields[25];//浏览器版本

            if (StringUtils.isEmpty(serverTime) || StringUtils.isEmpty(memberId)) {
                logger.info("serverTime & memberId is null serverTime:" + serverTime + ".memberId" + memberId);
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

            //用户模块活跃用户
            //设置默认的浏览器对象(因为活跃用户指标并不需要浏览器维度，所以赋值为空)
            BrowserDimension defaultBrowserDimension = new BrowserDimension("", "");
            statsCommonDimension.setKpiDimension(activeMemberKpi);
            this.k.setBrowserDimension(defaultBrowserDimension);
            this.k.setStatsCommonDimension(statsCommonDimension);
            this.v.setId(memberId);
            context.write(this.k, this.v);//输出

            //浏览器模块活跃用户
            statsCommonDimension.setKpiDimension(activeBrowserMemberKpi);
            BrowserDimension browserDimension = new BrowserDimension(browserName, browserVersion);
            this.k.setBrowserDimension(browserDimension);
            this.k.setStatsCommonDimension(statsCommonDimension);
            context.write(this.k, this.v);//输出
        }
    }
}