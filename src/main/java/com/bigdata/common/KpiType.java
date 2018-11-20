package com.bigdata.common;

/**
 * 统计指标的枚举
 * @ClassName: KpiType
 * @Description: TODO
 * @Author: xqg
 * @Date: 2018/11/3 22:12
 */
public enum  KpiType {
    NEW_USER("new_user"),//新增用户
    BROWSER_NEW_USER("browser_new_user"),//新增浏览器用户
    ACTIVE_USER("active_user"),//活跃用户
    BROWSER_ACTIVE_USER("browser_active_user"),//浏览器活跃用户
    ACTIVE_MEMBER("active_member"),//活跃会员
    BROWSER_ACTIVE_MEMBER("browser_active_member"),//浏览器活跃会员
    NEW_MEMBER("new_member"),//新增会员
    BROWSER_NEW_MEMBER("browser_new_member"),//浏览器新增会员
    MEMBER_INFO("member_info"),//会员信息
    SESSION("session"),//会话
    BROWSER_SESSION("browser_session"),//浏览器会话
    HOURLY_ACTIVE_USER("hourly_active_user"),//时长分析
    PAGEVIEW("pageview"),//pv
    BROWSER_PAGEVIEW("browser_pageview"),//浏览器pv
    LOCAL("local")//地区
    ;

    public String kpiName;

    KpiType(String kpiName) {
        this.kpiName = kpiName;
    }

    /**
     * 根据kpi的name获取对应的指标
     * @param name
     * @return
     */
    public static KpiType valueOfKpiName(String name){
        for (KpiType kpi : values()){
            if(kpi.kpiName.equals(name)){
                return kpi;
            }
        }
        return null;
    }
}
