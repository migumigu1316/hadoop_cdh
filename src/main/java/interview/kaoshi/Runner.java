package interview.kaoshi;

import java.io.IOException;
import java.util.Scanner;

import static interview.kaoshi.ReadFile.longStart;

/**
 * @ClassName: Runner
 * @Description: TODO
 * @Author: xqg
 * @Date: 2018/11/9 20:04
 */
public class Runner {
    public static void main(String[] args) throws IOException {
        System.out.println("请输入一个ip:");
        Scanner scanner = new Scanner(System.in);
        String sc_ip = scanner.next();

        //调用ip转成数字的类
        IPToID ip2ID = new IPToID();

        long ip = ip2ID.IPToID(sc_ip);
//        System.out.println(ip);
        new ReadFile(
                "F:\\IDEA_WorkSpace\\hadoop_cdh\\src\\main\\java\\interview\\kaoshi\\ip_rules");
//        BinarySearch binarySearch = new BinarySearch(,ip);
    }
}
