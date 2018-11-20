package interview.kaoshi;

import java.io.*;
import java.util.Scanner;

/**
 * @ClassName: ReadFile
 * @Description: TODO
 * @Author: xqg
 * @Date: 2018/11/9 16:52
 */
public class ReadFile {
    IPToID ip2ID = new IPToID();

    static long[] longStart = new long[4];
    long[] longEnd = new long[4];
    String[] StringCode = new String[4];

    public ReadFile(String path) throws IOException {
        //读文件
        FileReader fileReader = new FileReader(path);
        //使用字符缓冲流实现文本的复制
        //1.创建字符缓冲读入流,写出流
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String data = null;

        int i = 0;
        //按行读

        while ((data = bufferedReader.readLine()) != null) {
            //0.0.0.0 1.0.0.255   1000000000
            String[] fields = data.split(" ");
            String start_ip = fields[0];
            String end_ip = fields[1];
            String code= fields[2];

            long ipStart = ip2ID.IPToID(start_ip);
            long ipEnd = ip2ID.IPToID(end_ip);

            longStart[i] = ipStart;
            longEnd[i] = ipEnd;
            StringCode[i] = code;
        }
        //关闭流
        bufferedReader.close();
    }

}
