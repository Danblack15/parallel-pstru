package org.example.lab_4;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class main {

    public static final int THREADS = 8;
    public static final int ITERATIONS = 10000000;
    public static final double NSEC = 1_000_000_000.0;
    public static final int MAP_SIZE = 3;
    public static final int SAMPLES = 5;

    private static final String[] KEYS = {"key0", "key1", "key2"};

    public static void main(String[] args) {

        System.out.println("=== Тестирование потокобезопасности и производительности коллекций ===\n");

        // --- Демонстрация ошибок ---
        System.out.println("--- Корректность при конкурентных инкрементах ---");
        System.out.println("  (ожидаемый итог: " + THREADS * ITERATIONS + " инкрементов)\n");

        // HashMap — небезопасная запись (read + put без синхронизации)
        checkCorrectness("HashMap           ", new HashMap<>());

        // Hashtable — внешняя синхронизация на объекте (synchronized(map))
        checkCorrectness("Hashtable         ", new Hashtable<>());

        // synchronizedMap — внешняя синхронизация на объекте-обёртке
        checkCorrectness("SynchronizedMap   ", Collections.synchronizedMap(new HashMap<>()));

        // ConcurrentHashMap — атомарный merge (CAS внутри)
        checkCorrectness("ConcurrentHashMap ", new ConcurrentHashMap<>());

        // --- Замер производительности ---
        System.out.println("\n--- Замер производительности ---");

        double hashMapTime   = measure(new HashMap<>())                              / NSEC;
        double hashTableTime = measure(new Hashtable<>())                            / NSEC;
        double syncMapTime   = measure(Collections.synchronizedMap(new HashMap<>())) / NSEC;
        double cHashMapTime  = measure(new ConcurrentHashMap<>())                    / NSEC;

        System.out.println("\nВремя выполнения (последний из " + SAMPLES + " прогонов):");
        System.out.printf("  HashMap            : %.4f с%n", hashMapTime);
        System.out.printf("  Hashtable          : %.4f с%n", hashTableTime);
        System.out.printf("  SynchronizedMap    : %.4f с%n", syncMapTime);
        System.out.printf("  ConcurrentHashMap  : %.4f с%n", cHashMapTime);
    }

    // -----------------------------------------------------------------------
    // Проверка корректности
    //   safe=false → небезопасная запись (для демонстрации гонки у HashMap)
    //   safe=true  → атомарная запись (для потокобезопасных коллекций)
    // -----------------------------------------------------------------------
    private static void checkCorrectness(String label, Map<String, Integer> map) {

        for (String key : KEYS) map.put(key, 0);

        AtomicInteger exceptions = new AtomicInteger(0);
        ExecutorService pool = Executors.newFixedThreadPool(THREADS);
        List<Callable<Void>> tasks = new ArrayList<>();

        for (int i = 0; i < THREADS; i++) {
            tasks.add(() -> {
                for (int iter = 0; iter < ITERATIONS; iter++) {
                    String key = KEYS[iter % MAP_SIZE];
                    try {
                        map.merge(key, 1, Integer::sum);
                    } catch (Exception e) {
                        exceptions.incrementAndGet();
                    }
                }
                return null;
            });
        }

        try {
            for (Future<Void> f : pool.invokeAll(tasks)) {
                try { f.get(); }
                catch (ExecutionException e) { exceptions.incrementAndGet(); }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        pool.shutdown();

        int expected = THREADS * ITERATIONS;
        int actual = 0;
        for (String key : KEYS) {
            Integer v = map.get(key);
            actual += (v == null ? 0 : v);
        }
        int lost = expected - actual;
        int errors = exceptions.get() + lost;

        System.out.printf("  %-22s фактически: %6d  потеряно: %5d  исключений: %d  → %s%n",
                label, actual, lost, exceptions.get(),
                errors == 0 ? "OK" : "ОШИБКА (гонка данных!)");
    }

    // -----------------------------------------------------------------------
    // Замер производительности (правильные атомарные записи для всех)
    // -----------------------------------------------------------------------
    private static long measure(Map<String, Integer> map) {

        String name = map.getClass().getSimpleName();
        System.out.print("  Measuring " + name + " ...");

        long start = 0, stop = 0;

        for (int k = 0; k < SAMPLES; k++) {
            map.clear();
            for (String key : KEYS) map.put(key, 0);

            start = System.nanoTime();

            ExecutorService pool = Executors.newFixedThreadPool(THREADS);
            List<Callable<Void>> tasks = new ArrayList<>();

            for (int i = 0; i < THREADS; i++) {
                tasks.add(() -> {
                    for (int iter = 0; iter < ITERATIONS; iter++) {
                        String key = KEYS[iter % MAP_SIZE];
                        map.merge(key, 1, Integer::sum);
                    }
                    return null;
                });
            }

            try {
                for (Future<Void> f : pool.invokeAll(tasks)) f.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

            pool.shutdown();
            stop = System.nanoTime();
        }

        System.out.println(" done.");
        return stop - start;
    }
}