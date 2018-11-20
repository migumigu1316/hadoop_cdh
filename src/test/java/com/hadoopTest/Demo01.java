package com.hadoopTest;

public class Demo01 {
    public static void main(String[] args) {
        String str = "192.168.152.1^A1541085633.638^A192.168.152.10^A/demo.jsp?en=e_pv&p_url=http%3A%2F%2Flocalhost%3A8088%2Fdemo2.jsp&p_ref=http%3A%2F%2Flocalhost%3A8088%2Fdemo4.jsp&tt=%E6%B5%8B%E8%AF%95%E9%A1%B5%E9%9D%A22&ver=1&pl=website&sdk=js&u_ud=6C343901-4707-4736-9F57-EBF07FA3D8C6&u_mid=liyadong&u_sd=DA82312A-444D-4786-B6A4-F05139C1E347&c_time=1541070931119&l=zh-CN&b_iev=Mozilla%2F5.0%20(Windows%20NT%2010.0%3B%20WOW64)%20AppleWebKit%2F537.36%20(KHTML%2C%20like%20Gecko)%20Chrome%2F70.0.3538.77%20Safari%2F537.36&b_rst=1366*768";
        String[] split = str.split("\\^A");
        System.out.println(split.length);
        for (int i=0;i<split.length;i++){
            System.out.println(split[i]);
        }
    }
}
