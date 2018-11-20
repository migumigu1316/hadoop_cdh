package interview;

/**
 * @ClassName: quickSort
 * @Description: TODO
 * @Author: xqg
 * @Date: 2018/11/8 10:37
 */
public class quickSort {
    public static void main(String[] args) {
        int[] a = {5, 3, 6, 7};
        //quickSort(数组,数组下标[左],数组长度[下标右])
        quickSort(a, 0, a.length - 1);
        for (int i = 0; i < a.length; i++)
            System.out.print(a[i] + " ");
    }

    public static void quickSort(int[] array, int left, int right) {
        if (left < right) {
            int pivot = array[left];
            int low = left;
            int high = right;
            while (low < high) {
                while (low < high && array[high] >= pivot)
                    high--;
                swap(array, low, high);
                while (low < high && array[low] <= pivot)
                    low++;
                swap(array, high, low);
            }
            array[low] = pivot;
            quickSort(array, left, low - 1);
            quickSort(array, low + 1, right);
        }
    }

    public static void swap(int[] t, int a, int b) {
//        int temp = t[a];
//        t[a] = t[b];
//        t[b] = temp;
        t[a] = t[a] ^ t[b];
        t[b] = t[a] ^ t[b];
        t[a] = t[a] ^ t[b];
    }

}
