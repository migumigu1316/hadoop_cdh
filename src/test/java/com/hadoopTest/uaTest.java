package com.hadoopTest;

import com.bigdata.etl.ip.userAgentUtil;

public class uaTest {
    public static void main(String[] args) {
        System.out.println(userAgentUtil.parserUserAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.26 Safari/537.36 Core/1.63.6756.400 QQBrowser/10.3.2565.400"));
        System.out.println(userAgentUtil.parserUserAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.106 BIDUBrowser/8.7 Safari/537.36"));
        System.out.println(userAgentUtil.parserUserAgent("Mozilla/5.0 (Windows NT 10.0; WOW64; Trident/7.0; .NET4.0C; .NET4.0E; .NET CLR 2.0.50727; .NET CLR 3.0.30729; .NET CLR 3.5.30729; rv:11.0) like Gecko"));
    }
}
