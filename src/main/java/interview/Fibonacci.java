package interview;

/**
 * @ClassName: Fibonacci
 * @Description: TODO 实现Fibonacci(即斐波那契额)数列
 * @Author: xqg
 * @Date: 2018/11/8 10:17
 */
public class Fibonacci {
    public static int func(int n) throws Exception {
        if (n == 0) {
            throw new Exception("参数错误！");
        }
        if (n == 1 || n == 2) {
            return 1;
        } else {
            return func(n - 1) + func(n - 2);//自己调用自己
        }
    }

    public static void main(String[] args) throws Exception {
        for (int i = 1; i <= 10; i++) {
            System.out.print(func(i) + "\t");
            //1 1 2 3 5 8 13 21 34 55
        }
    }
}
