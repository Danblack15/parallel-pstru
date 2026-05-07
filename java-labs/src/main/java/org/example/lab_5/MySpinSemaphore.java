package org.example.lab_5;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class MySpinSemaphore extends Semaphore {

    private final AtomicInteger permits;

    public MySpinSemaphore(int initialPermits) {
        super(initialPermits);
        this.permits = new AtomicInteger(initialPermits);
    }

    @Override
    public void acquire() throws InterruptedException {
        while (true) {
            if (Thread.interrupted()) throw new InterruptedException();

            int current = permits.get();
            if (current > 0 && permits.compareAndSet(current, current - 1)) {
                System.out.printf("  [CAS-семафор] захвачен потоком %s  (осталось разрешений: %d)%n",
                        Thread.currentThread().getName(), permits.get());
                return;
            }
            Thread.onSpinWait();
        }
    }

    @Override
    public void release() {
        int current = permits.incrementAndGet();
        System.out.printf("  [CAS-семафор] освобождён потоком %s  (осталось разрешений: %d)%n",
                Thread.currentThread().getName(), current);
    }

    @Override
    public int availablePermits() {
        return permits.get();
    }
}
