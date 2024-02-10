import java.util.*;
import java.util.concurrent.*;

public class Main {

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        final ExecutorService threadPool = Executors.newFixedThreadPool(25);
        List<Callable<Integer>> calls = new ArrayList<>();
        Callable<Integer> logic;
        for (int ii = 0; ii < 25; ii++) {
            String text = generateText("aab", 30_000);
            logic = () -> {
                int maxSize = 0;
                for (int i = 0; i < text.length(); i++) {
                    for (int j = 0; j < text.length(); j++) {
                        if (i >= j) {
                            continue;
                        }
                        boolean bFound = false;
                        for (int k = i; k < j; k++) {
                            if (text.charAt(k) == 'b') {
                                bFound = true;
                                break;
                            }
                        }
                        if (!bFound && maxSize < j - i) {
                            maxSize = j - i;
                        }
                    }
                }
                System.out.println(text.substring(0, 100) + " -> " + maxSize);
                return Integer.valueOf(maxSize);
            };
            calls.add(logic);
        }

        Integer max = Integer.MIN_VALUE;

        long startTs = System.currentTimeMillis(); // start time
        final List<Future<Integer>> tasks = threadPool.invokeAll(calls);
        for (Future<Integer> task : tasks) {
            final Integer result = task.get();
            if (result > max) {
                max = result;
            }
        }
        long endTs = System.currentTimeMillis(); // end time

        System.out.println("Time: " + (endTs - startTs) + "ms");
        System.out.println("Max interval: " + max);
    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }
}