
import java.util.Arrays;

public class TestMergeSort {

    private static void test(int[] input, int cutoff) {
        int[] expected = input.clone();
    }

    public static void main(String[] args) {
        int[] cutoffs = {1, 2, 7, 10, 40};

        int[][] testCases = {
            {},
            {5},
            {3, 1, 2},
            {5, 5, 5, 5},
            {-3, 8, 0, -1, 4},
            {1, 2, 3, 4, 5},
            {9, 8, 7, 6, 5, 4, 3, 2, 1},
            {5, 2, 8, 2, 1, 9, 5}
        };

        for (int cutoff : cutoffs) {
            for (int[] input : testCases) {
                int[] actual = input.clone();

                MergeSort.sort(actual, cutoff);

                int[] expected = input.clone();

                // Kiểm tra thứ tự tăng dần và giữ nguyên các phần tử.
                Arrays.sort(expected);

                if (!Arrays.equals(actual, expected)) {
                    throw new AssertionError(
                        "Failed! cutoff=" + cutoff
                        + ", input=" + Arrays.toString(input)
                        + ", actual=" + Arrays.toString(actual)
                    );
                }
            }
        }

        System.out.println("All tests passed!");
    }
}
