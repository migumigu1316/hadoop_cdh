package com.hadoopTest;


import com.bigdata.Util.IpUtil;

/**
 * @ClassName: IpTest
 * @Description: TODO
 * @Author: xqg
 * @Date: 2018/10/31 21:13
 */
public class IpTest {
    public static void main(String[] args) {
//        System.out.println(IPSeeker.getInstance().getCountry("114.231.112.23"));

        System.out.println(IpUtil.getRegionInfoByIp("114.231.112.23"));

        try {
            System.out.println(IpUtil.parserIp1("http://ip.taobao.com/service/getIpInfo.php?ip=114.231.112.23","utf-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
