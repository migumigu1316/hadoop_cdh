package interview.kaoshi;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Scanner;

import static java.lang.System.exit;

public class TestDemo {
    public static void main(String[] args) throws  Exception{
        System.out.println("please input a ip :");
        Scanner scanner = new Scanner(System.in);
        String sc_ip = scanner.next();
//        System.out.println(sc_ip);
        long sc_ip_long=IP2Long(sc_ip);
        BufferedReader bufferedReader = new BufferedReader(new FileReader("F:\\IDEA_WorkSpace\\hadoop_cdh\\src\\main\\java\\interview\\kaoshi\\ip_rules"));
        String data=null;
        long [] longStart =new long[4];
        long [] longEnd =new long[4];
        long [] code =new long[4];
        int i=0;
        while ((data=bufferedReader.readLine())!=null){
            String ip = data.split(" ")[0];//起始的ip
            longStart[i] = IP2Long(ip);
            ip = data.split(" ")[1];//起始的ip
            longEnd[i] = IP2Long(ip);
            code[i]=Long.valueOf(data.split(" ")[2]);
             i++;
        }//循环将ip转成long 存储在数组里
        int search = search(IP2Long(sc_ip), longStart);
        if(sc_ip_long>longStart[search] && sc_ip_long<longEnd[search]){
            System.out.println(sc_ip +"--> code:"+code[search]);
        }else {
            System.out.println("search error ,you can input again.");
        }
    }
    public static long IP2Long(String  ip) {
        long a=0;
        String[] split = ip.split("\\.");
        for (String s:split){
            a=a<<8;
            int i = Integer.valueOf(s);
            if (i>255||i<0){
                System.out.println("ip illegal!!");
                exit(0);
            }
            a+=i;
        }
        return a;
    }
    public  static  int search (long l ,long [] longs){
        int low =0;
        int high =longs.length-1;
        int mid=0;
        while (low<=high){
            mid =(low+high)>>1;
            if(l<longs[mid]){
                high=mid-1;
            }else if (l>longs[mid]){
                low=mid+1;
            }else{
                return mid;
            }
        }
        return low-1;
    }
}
