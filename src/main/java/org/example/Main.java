package org.example;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.IntStream;

public class Main {
    static BlockingQueue<String> blockingQueueA = new ArrayBlockingQueue<>(100);
    static BlockingQueue<String> blockingQueueB = new ArrayBlockingQueue<>(100);
    static BlockingQueue<String> blockingQueueC = new ArrayBlockingQueue<>(100);
    static AtomicBoolean isFinished = new AtomicBoolean(false);

    public static void main(String[] args) {
        //
        new Thread(() -> {
            String longest = count('a', blockingQueueA);
            System.out.println("max 'a': " + longest);
        }).start();
        //
        new Thread(() -> {
            String longest = count('b', blockingQueueB);
            System.out.println("max 'b': " + longest);
        }).start();
        //
        new Thread(() -> {
            String longest = count('c', blockingQueueC);
            System.out.println("max 'c': " + longest);
        }).start();
        //
        new Thread(() -> {
            String[] texts = new String[10_000];
            for (int i = 0; i < texts.length; i++) {
                texts[i] = generateText("abc", 100_000);
                final String currentS = texts[i];
                try {
                    blockingQueueA.put(currentS);
                    blockingQueueB.put(currentS);
                    blockingQueueC.put(currentS);
                } catch (InterruptedException e) {
                    return;
                }
            }
            isFinished.set(true);
        }).start();
    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }

    public static String count(char abc, BlockingQueue<String> queue) {
        String currentLongest = "null";
        long reps = 0;
        while (!isFinished.get()) {
            while (!queue.isEmpty()) {
                try {
                    String s1 = queue.take();
                    char[] array = s1.toCharArray();
                    long cStream = IntStream.range(0, array.length).mapToObj(i -> array[i]).filter(q -> q.equals(abc)).count();
                    if (cStream > reps) {
                        currentLongest = s1;
                        reps = cStream;
                    }
                } catch (InterruptedException e) {
                    System.out.println(":(");
                }
            }
        }
        return currentLongest;
    }
}