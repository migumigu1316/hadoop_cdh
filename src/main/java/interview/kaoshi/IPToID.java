package interview.kaoshi;

/**
 * @ClassName: IPToID
 * @Description: TODO
 * @Author: xqg
 * @Date: 2018/11/9 19:19
 */
public class IPToID {
    public long IPToID(String ipAddress) {
        //ip转数字
        long a = 0;
        String[] ip2num = ipAddress.split("\\.");
//            System.out.println(ip2num);
        for (String s : ip2num) {
            a *= 256;
            int integer = Integer.valueOf(s);
            a += integer;
//                System.out.println(a);
        }
        long result = a;
        return result;
    }
}
