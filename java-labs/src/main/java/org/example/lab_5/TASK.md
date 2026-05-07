# Java util concurrent (создание собственного считающего семафора)
## Необходимо:

- Написать программу, которая демонстрирует работу считающего семафора
- Написать собственную реализацию семаформа (наследование от стандартного с переопределением функций) и использовать его

```java
package com.example;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class Main {

    public static final int THREADS = 4;
    public static final int COUNT = 2;
    public static MySemaphore mySemaphore = new MySemaphore(COUNT);
    public static Semaphore regularSemaphore = new Semaphore(COUNT);

    public static void main(String[] args) {
        System.out.println("-------------------\nRegular semaphore:\n-------------------");
        runTask(regularSemaphore);
        System.out.println("--------------\nMy semaphore:\n--------------");
        runTask(mySemaphore);
    }

    private static void runTask(Semaphore semaphore) {
        ExecutorService es = Executors.newFixedThreadPool(THREADS);

        List<Callable<String>> tasks = new ArrayList<>();
        List<Future<String>> results = new ArrayList<>();

        for (int i = 0; i < THREADS; i++) {
            tasks.add(() -> {
                String threadName = Thread.currentThread().getName();

                // ToDo: работа с семафором

                return "Thread " + threadName + " done";
            });
        }

        // invoke all the tasks
        try {
            results = es.invokeAll(tasks);
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }

        // shutdown executor service
        es.shutdown();
    }
}
```

Семафор (можно написать полностью свою реализацию без ReentrantLock):

```java
package com.example;

import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class MySemaphore extends Semaphore {

    private int max;
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition permitsAvailable = lock.newCondition();
    private int permits;

    public MySemaphore(int initialPermits) {
        super(initialPermits);
        lock.lock();
        try {
            permits = initialPermits;
        } finally {
            lock.unlock();
        }

    }
    @Override
    public void acquire() throws InterruptedException {
        // ToDo: написать код
    }

    @Override
    public void release() {
        // ToDo: написать код
    }

    @Override
    public int availablePermits() {
        return permits;
    }
}

```
