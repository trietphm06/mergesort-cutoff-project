import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

public final class Main {
    private static final int[] CUTOFFS = {1, 3, 5, 7, 8, 10, 12, 15, 20, 25, 30, 40, 50, 64, 80, 100, 128, 160};
    private static int WARMUPS = 5;
    private static int REPETITIONS = 30;

    private record Dataset(String pattern, int n, Path path) {}

    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            System.err.println("Usage: java -cp out Main <data-directory> <output-csv>");
            System.exit(1);
        }
        Path dataDir = Path.of(args[0]);
        Path output = Path.of(args[1]);
        if (args.length > 2) WARMUPS = Integer.parseInt(args[2]);
        if (args.length > 3) REPETITIONS = Integer.parseInt(args[3]);
        List<Dataset> datasets = discover(dataDir);
        if (datasets.isEmpty()) throw new IllegalStateException("No .txt datasets found in " + dataDir);
        Files.createDirectories(output.toAbsolutePath().getParent());

        try (var writer = Files.newBufferedWriter(output)) {
            writer.write("pattern,n,cutoff,run,time_ns");
            writer.newLine();
            for (Dataset ds : datasets) {
                int[] original = readInts(ds.path());
                if (original.length != ds.n()) throw new IllegalStateException("Dataset size mismatch: " + ds.path());

                for (int cutoff : CUTOFFS) {
                    int[] a = original.clone();
                    MergeSort.sort(a, cutoff);
                    verify(a, ds.path(), cutoff);
                }

                List<Integer> order = new ArrayList<>();
                for (int c : CUTOFFS) order.add(c);
                java.util.Random rnd = new java.util.Random(42);
                for (int round = 0; round < WARMUPS + REPETITIONS; round++) {
                    java.util.Collections.shuffle(order, rnd);
                    for (int cutoff : order) {
                        int[] a = original.clone();
                        long start = System.nanoTime();
                        MergeSort.sort(a, cutoff);
                        long elapsed = System.nanoTime() - start;
                        verify(a, ds.path(), cutoff);
                        if (round >= WARMUPS) {
                            writer.write(String.format(Locale.ROOT, "%s,%d,%d,%d,%d",
                                    ds.pattern(), ds.n(), cutoff, round - WARMUPS + 1, elapsed));
                            writer.newLine();
                        }
                    }
                }
                writer.flush();
                System.out.printf(Locale.ROOT, "Done: %-14s n=%d%n", ds.pattern(), ds.n());
            }
        }
        System.out.println("Benchmark complete: " + output);
    }

    private static List<Dataset> discover(Path dir) throws IOException {
        List<Dataset> result = new ArrayList<>();
        try (Stream<Path> paths = Files.list(dir)) {
        paths.filter(p -> p.getFileName().toString()
                .matches("(random|sorted|reverse|nearly_sorted)_n\\d+\\.txt"))
             .sorted(Comparator.comparing(
                 p -> p.getFileName().toString()))
             .forEach(p -> {
                 String name = p.getFileName().toString()
                         .replace(".txt", "");

                 int marker = name.lastIndexOf("_n");
                 String pattern = name.substring(0, marker);
                 int n = Integer.parseInt(
                         name.substring(marker + 2));

                 result.add(new Dataset(pattern, n, p));
             });
        }

        return result;
    }

    private static int[] readInts(Path path) throws IOException {
        String content = Files.readString(path).trim();
        if (content.isEmpty()) return new int[0];
        String[] tokens = content.split("\\s+");
        int[] a = new int[tokens.length];
        for (int i = 0; i < tokens.length; i++) a[i] = Integer.parseInt(tokens[i]);
        return a;
    }

    private static void verify(int[] a, Path source, int cutoff) {
        for (int i = 1; i < a.length; i++) {
            if (a[i - 1] > a[i]) {
                throw new IllegalStateException("Not sorted: " + source + ", cutoff=" + cutoff);
            }
        }
    }
}