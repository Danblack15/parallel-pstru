package org.example.lab_5;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class Main {

    public static final int THREADS = 4;
    public static final int COUNT = 2;
    public static MySemaphore mySemaphore = new MySemaphore(COUNT);
    public static MySpinSemaphore mySpinSemaphore = new MySpinSemaphore(COUNT);
    public static Semaphore regularSemaphore = new Semaphore(COUNT);

    public static void main(String[] args) {
        System.out.println("-------------------\nRegular semaphore:\n-------------------");
        runTask(regularSemaphore);
        System.out.println("--------------\nMy semaphore:\n--------------");
        runTask(mySemaphore);
        System.out.println("--------------\nCAS semaphore:\n--------------");
        runTask(mySpinSemaphore);
    }

    private static void runTask(Semaphore semaphore) {
        ExecutorService es = Executors.newFixedThreadPool(THREADS);

        List<Callable<String>> tasks = new ArrayList<>();
        List<Future<String>> results = new ArrayList<>();

        for (int i = 0; i < THREADS; i++) {
            tasks.add(() -> {
                String threadName = Thread.currentThread().getName();

                semaphore.acquire();
                try {
                    System.out.println("Thread " + threadName + " entered critical section"
                            + " | permits left: " + semaphore.availablePermits());
                    Thread.sleep(500);
                } finally {
                    semaphore.release();
                }

                return "Thread " + threadName + " done";
            });
        }

        try {
            results = es.invokeAll(tasks);
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }

        for (Future<String> result : results) {
            try {
                System.out.println(result.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        es.shutdown();
    }
}
