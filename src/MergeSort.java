public final class MergeSort {
    private MergeSort() {}

    public static void sort(int[] a, int cutoff) {
        if (a == null) {
            throw new IllegalArgumentException("Input array cannot be null");
        }

        if (cutoff < 1) {
            throw new IllegalArgumentException("cutoff must be >= 1");
        }

        int[] aux = new int[a.length];

        sort(a, aux, 0, a.length - 1, cutoff);
    }

    private static void sort(
        int[] a, int[] aux, int lo, int hi, int cutoff
    ) {
        if (hi <= lo) {
            return;
        }

        if (hi - lo + 1 <= cutoff) {
            InsertionSort.sort(a, lo, hi);
            return;
        }

        int mid = lo + (hi - lo) / 2;

        sort(a, aux, lo, mid, cutoff);
        sort(a, aux, mid + 1, hi, cutoff);

        merge(a, aux, lo, mid, hi);
    }

    private static void merge(
        int[] a, int[] aux, int lo, int mid, int hi
    ) {
        for (int k = lo; k <= hi; k++) {
            aux[k] = a[k];
        }

        int i = lo;
        int j = mid + 1;

        for (int k = lo; k <= hi; k++) {
            if (i > mid) {
                a[k] = aux[j++];
            } else if (j > hi) {
                a[k] = aux[i++];
            } else if (aux[i] <= aux[j]) {
                a[k] = aux[i++];
            } else {
                a[k] = aux[j++];
            }
        }
    }
}
