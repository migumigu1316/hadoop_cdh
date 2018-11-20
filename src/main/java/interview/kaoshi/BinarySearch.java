package interview.kaoshi;

/**
 * @ClassName: BinarySearch
 * @Description: TODO
 * @Author: xqg
 * @Date: 2018/11/9 16:24
 */
public class BinarySearch {
    public int BinarySearch(long[] arr, long key) {
        int low = 0;
        int heigth = arr.length - 1;
        while (low <= heigth) {
            int m = (low + heigth) >> 1;// 移位运算符,向右移一位
            if (key < arr[m]) {
                heigth = m - 1;
            } else if (key > arr[m]) {
                low = m + 1;
            } else if (key == arr[m]) {
                return m;
            }
        }
        return -1;
    }

    public static void main(String[] args) {


    }
}
