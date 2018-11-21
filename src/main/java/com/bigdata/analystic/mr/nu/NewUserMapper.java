package com.bigdata.analystic.mr.nu;

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
 *〈一句话功能简述〉<br>
 *〈NewUserMapper---mapper函数 简单的封装〉
 *
 * 用户模块下的新增用户
 *
 * 注意点：每次测试前都要清空数据库中的数据
 * 新建查询---执行所有的SQL语句
 * 如下：
 * truncate dimension_browser;
 * truncate dimension_currency_type;
 * truncate dimension_date;
 * truncate dimension_event;
 * truncate dimension_inbound;
 * truncate dimension_kpi;
 * truncate dimension_location;
 * truncate dimension_os;
 * truncate dimension_payment_type;
 * truncate dimension_platform;
 * truncate event_info;
 * truncate order_info;
 * truncate stats_device_browser;
 * truncate stats_device_location;
 * truncate stats_event;
 * truncate stats_hourly;
 * truncate stats_inbound;
 * truncate stats_order;
 * truncate stats_user;
 * truncate stats_view_depth;
 */

/**
 * TODO 计算launch事件中uuid唯一的个数,launch事件,en = e_l的事件
 * =====>触发条件,第一次登陆,或者清空缓存
 *
 * new_install_user计算规则：计算launch事件中，uuid的唯一个数。
 * total_user计算规则：同一个维度，前一天的总用户+当天新增用户。(只按照天来统计数据，但是提供按照其他时间维度统计的方式)
 * * 最终数据保存：stats_user和stats_device_browser。涉及到的列(除了维度列和created列外)：
 *  new_install_users和total_install_users。涉及到其他表有dimension_platform、dimension_date、dimension_browser。
 */
public class NewUserMapper extends Mapper<LongWritable,Text,StatsUserDimension,TimeOutputValue> {
    private static final Logger logger = Logger.getLogger(NewUserMapper.class);
    private StatsUserDimension k = new StatsUserDimension();
    private TimeOutputValue v = new TimeOutputValue();

    private KpiDimension newUserKpi = new KpiDimension(KpiType.NEW_USER.kpiName);
    private KpiDimension newBrowserUserKpi = new KpiDimension(KpiType.BROWSER_NEW_USER.kpiName);

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String line = value.toString();
        if(StringUtils.isEmpty(line)){
            return ;
        }

        //拆分
        String[] fields = line.split("\u0001");
        //en是事件名称
        String en = fields[2];
        if(StringUtils.isNotEmpty(en) && en.equals(Constants.EventEnum.LANUCH.alias)){
            //获取想要的字段
            String serverTime = fields[1];
            String platform = fields[13];//平台
            String uuid = fields[3];//uuid
            String browserName = fields[24];//浏览器名字
            String browserVersion = fields[25];//浏览器版本

            if(StringUtils.isEmpty(serverTime) || StringUtils.isEmpty(uuid)){
                logger.info("serverTime & uuid is null serverTime:"+serverTime+".uuid"+uuid);
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

            //用户模块新增用户
            //设置默认的浏览器对象(因为新增用户指标并不需要浏览器维度，所以赋值为空)
            BrowserDimension defaultBrowserDimension = new BrowserDimension("","");
            statsCommonDimension.setKpiDimension(newUserKpi);
            this.k.setBrowserDimension(defaultBrowserDimension);
            this.k.setStatsCommonDimension(statsCommonDimension);
            this.v.setId(uuid);
            context.write(this.k,this.v);//输出

            //浏览器模块新增用户
            statsCommonDimension.setKpiDimension(newBrowserUserKpi);
            BrowserDimension browserDimension = new BrowserDimension(browserName,browserVersion);
            this.k.setBrowserDimension(browserDimension);
            this.k.setStatsCommonDimension(statsCommonDimension);
            context.write(this.k,this.v);//输出
        }
    }
}