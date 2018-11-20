package com.hadoopCDH.etl.ip;

import cz.mallat.uasparser.OnlineUpdater;
import cz.mallat.uasparser.UASparser;
import org.apache.log4j.Logger;

import java.io.IOException;

public class UserAgentUtil {
    private static Logger logger = Logger.getLogger(UserAgentUtil.class);
    static UASparser uasParser = null;

    // static 代码块, 初始化uasParser对象
    static {
        try {
            uasParser = new UASparser(OnlineUpdater.getVendoredInputStream());
        } catch (IOException e) {
            logger.error("获取uasparser对象异常",e);

        }
    }

    /**
     * 解析浏览器的user agent字符串，返回UserAgentInfo对象。<br/>
     * 如果user agent为空，返回null。如果解析失败，也直接返回null。
     * @param userAgent   要解析的user agent字符串
     * @return 返回具体的值
     */
    public static UserAgentInfo analyticUserAgent(String userAgent) {
        UserAgentInfo result = null;
        if (!(userAgent == null || userAgent.trim().isEmpty())) {
            // 此时userAgent不为null，而且不是由全部空格组成的
            try {
                cz.mallat.uasparser.UserAgentInfo info = null;
                info = uasParser.parse(userAgent);
                result = new UserAgentInfo();

                //将UserAgentInfo中的值赋值给result
                result.setBrowserName(info.getUaFamily());//浏览器名称
                result.setBrowserVersion(info.getBrowserVersionInfo());//浏览器版本
                result.setOsName(info.getOsFamily());//操作系统名称
                result.setOsVersion(info.getOsName());//操作系统
            } catch (IOException e) {
            // 出现异常，将返回值设置为null
                result = null;
            }
        }
        return result;
    }


    /**
     * 内部解析后的浏览器信息model对象
     * @author root
     */
    public static class UserAgentInfo {
        private String browserName; // 浏览器名称
        private String browserVersion; // 浏览器版本号
        private String osName; // 操作系统名称
        private String osVersion; // 操作系统版本号


        public String getBrowserName() {
            return browserName;
        }


        public void setBrowserName(String browserName) {
            this.browserName = browserName;
        }


        public String getBrowserVersion() {
            return browserVersion;
        }


        public void setBrowserVersion(String browserVersion) {
            this.browserVersion = browserVersion;
        }


        public String getOsName() {
            return osName;
        }


        public void setOsName(String osName) {
            this.osName = osName;
        }


        public String getOsVersion() {
            return osVersion;
        }


        public void setOsVersion(String osVersion) {
            this.osVersion = osVersion;
        }


        @Override
        public String toString() {
            return browserName + "\t" + browserVersion + "\t"
                    + osName + "\t" + osVersion;
        }
    }
}
