public final class InsertionSort {
    private InsertionSort() {}

public static void sort(int[] a, int lo, int hi) {
    for (int i = lo + 1; i <= hi; i++) {
        int current = a[i];
        int j = i - 1;

        while (j >= lo && a[j] > current) {
            a[j + 1] = a[j];
            j--;
        }

        a[j + 1] = current;
    }
}
}