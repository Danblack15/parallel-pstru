package org.example.lab_5;

import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class MySemaphore extends Semaphore {

    private final ReentrantLock lock = new ReentrantLock();
    private final Condition permitsAvailable = lock.newCondition();
    private int permits;

    public MySemaphore(int initialPermits) {
        super(initialPermits);
        this.permits = initialPermits;
    }

    @Override
    public void acquire() throws InterruptedException {
        lock.lock();
        try {
            while (permits == 0) {
                permitsAvailable.await();
            }
            permits--;
            System.out.println("[MySemaphore] acquired by " + Thread.currentThread().getName()
                    + ", permits left: " + permits);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void release() {
        lock.lock();
        try {
            permits++;
            System.out.println("[MySemaphore] released by " + Thread.currentThread().getName()
                    + ", permits left: " + permits);
            permitsAvailable.signal();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int availablePermits() {
        lock.lock();
        try {
            return permits;
        } finally {
            lock.unlock();
        }
    }
}