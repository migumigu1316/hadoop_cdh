package interview;

/**
 * @ClassName: ArrayTools
 * @Description: TODO
 * @Author: xqg
 * @Date: 2018/11/9 13:03
 */
public class ArrayTools {//程序的调用

    // 普通查找
    public static int search(int[] arr, int key) {
        for (int i = 0; i < arr.length; i++) {
            if (key == arr[i]) {
                return 1;
            }
        }
        return -1;
    }

    // 二分查找(利用下标)
//    public static int erFensearch(int[] arr, int key) {
//        int low = 0;
//        int heigth = arr.length - 1;
//        while (low <= heigth) {
//            int m = (low + heigth) >> 1;// 移位运算符,向右移一位
//            if (key < arr[m]) {
//                heigth = m - 1;
//            } else if (key > arr[m]) {
//                low = m + 1;
//            } else if (key == arr[m]) {
//                return m;
//            }
//        }
//        return -1;
//    }

    // 冒泡排序(相邻的两个数交换位置)
    public static void bubbleSort(int[] arr) {

        for (int i = 0; i < arr.length - 1; i++) {// 控制整体的次数
            for (int j = 0; j < arr.length - 1 - i; j++) {// 确定一个元素,需要比较的次数
                if (arr[j] > arr[j + 1]) {// 交换两个数的值
                    arr[j] = arr[j] ^ arr[j + 1];// 相邻的两个数交换位置
                    arr[j + 1] = arr[j] ^ arr[j + 1];
                    arr[j] = arr[j] ^ arr[j + 1];
                }
            }
        }
        for (int i = 0; i < arr.length; i++) {
            System.out.print(arr[i] + " ");
        }
    }

    // 选择排序
// 原理:通过比较,首先选出最小的数放在第一个位置,
// 然后选择其余的数中次小数,放在第二个位置,以此类推,直到排到有序数列
    public static void selectSort(int[] arr) {
        for (int i = 0; i < arr.length - 1; i++) {// 控制整体的次数
            for (int j = i; j < arr.length - 1 - i; j++) {// 确定一个元素,需要比较的次数
                if (arr[i] > arr[j + 1]) {// 交换两个数的值
                    arr[i] = arr[i] ^ arr[j + 1];// 一个元素跟后面的每个元素比较,交换位置
                    arr[j + 1] = arr[i] ^ arr[j + 1];
                    arr[i] = arr[i] ^ arr[j + 1];
                }
            }
        }
        for (int i = 0; i < arr.length; i++) {
            System.out.print(arr[i] + " ");
        }
    }
}
