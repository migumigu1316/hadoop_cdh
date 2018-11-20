package com.bigdata.analystic.mr.nm;

import com.bigdata.Util.JdbcUtil;
import com.bigdata.Util.MemberUtil;
import com.bigdata.analystic.model.StatsCommonDimension;
import com.bigdata.analystic.model.StatsUserDimension;
import com.bigdata.analystic.model.base.BrowserDimension;
import com.bigdata.analystic.model.base.DateDimension;
import com.bigdata.analystic.model.base.KpiDimension;
import com.bigdata.analystic.model.base.PlatformDimension;
import com.bigdata.analystic.model.value.map.TimeOutputValue;
import com.bigdata.common.DateEnum;
import com.bigdata.common.KpiType;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.sql.Connection;

/**
 * @ClassName: NewMemberMapper
 * @Description: TODO 新增会员
 * @Author: xqg
 * @Date: 2018/11/8 16:51
 */

public class NewMemberMapper extends Mapper<LongWritable, Text, StatsUserDimension, TimeOutputValue> {
    private static final Logger logger = Logger.getLogger(NewMemberMapper.class);
    private StatsUserDimension k = new StatsUserDimension();
    private TimeOutputValue v = new TimeOutputValue();

    private KpiDimension newMemberKpi = new KpiDimension(KpiType.NEW_MEMBER.kpiName);
    private KpiDimension newBrowserMemberKpi = new KpiDimension(KpiType.BROWSER_NEW_MEMBER.kpiName);

    private Connection conn = null;

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
//        Configuration conf = context.getConfiguration();
        conn = JdbcUtil.getConn();
        MemberUtil.deleteByDay(context.getConfiguration(), conn);
        //如果中途任务失败,重新跑任务时候,需要将之前添加的新增会员信息删除掉
//        MemberUtil.deleteMemberInfoByData(conf.get(GlobalConstants.RUNNING_DATE),conn);
    }

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
//        Configuration conf = context.getConfiguration();
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
//        String uuid = fields[3];//uuid
        String memberId = fields[4];//memberId
        String browserName = fields[24];//浏览器名字
        String browserVersion = fields[25];//浏览器版本

        if (StringUtils.isEmpty(serverTime) || StringUtils.isEmpty(memberId)) {
            logger.info("serverTime & memberId is null serverTime:" + serverTime + ".memberId" + memberId);
            return;
        }

//        if (memberId.equals("null")) {
//            return;
//        }
        //通过memberid判断是否是一个新增的会员还是一个老会员
        if(! MemberUtil.isNewMember(memberId,conn,context.getConfiguration())){
            logger.info("该会员是一个老会员.memberId:"+memberId);
            return;
        }

        //构造输出的key
        long stime = Long.valueOf(serverTime);
        PlatformDimension platformDimension = PlatformDimension.getInstance(platform);
        DateDimension dateDimension = DateDimension.buildDate(stime, DateEnum.DAY);
        StatsCommonDimension statsCommonDimension = this.k.getStatsCommonDimension();

        //为StatsCommonDimension设值
        statsCommonDimension.setDateDimension(dateDimension);
        statsCommonDimension.setPlatformDimension(platformDimension);

        //用户模块新增会员
        //设置默认的浏览器对象(因为新增会员指标并不需要浏览器维度，所以赋值为空)
        BrowserDimension defaultBrowserDimension = new BrowserDimension("", "");
        statsCommonDimension.setKpiDimension(newMemberKpi);
        this.k.setBrowserDimension(defaultBrowserDimension);
        this.k.setStatsCommonDimension(statsCommonDimension);
        this.v.setId(memberId);
        this.v.setTime(stime);
        context.write(this.k, this.v);//输出

        //浏览器模块新增会员
        statsCommonDimension.setKpiDimension(newBrowserMemberKpi);
        BrowserDimension browserDimension = new BrowserDimension(browserName, browserVersion);
        this.k.setBrowserDimension(browserDimension);
        this.k.setStatsCommonDimension(statsCommonDimension);
//        this.v.setTime(stime);
        context.write(this.k, this.v);//输出

    }

    @Override
    protected void cleanup(Context context) throws IOException, InterruptedException {
        JdbcUtil.close(conn, null, null);
    }
}