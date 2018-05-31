package org.liquidengine.leutil.service;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by ShchAlexander on 21.09.2017.
 */
public class Task<T> {

    private final CountDownLatch latch = new CountDownLatch(1);

    private Callable<T> callable;
    private AtomicBoolean called = new AtomicBoolean();
    private T result;

    public Task(Callable<T> callable) {
        this.callable = callable;
    }

    public T get() throws InterruptedException {
        if (result == null) {
            latch.await();
        }
        return result;
    }

    public void execute() throws Exception {
        if (called.compareAndSet(false, true)) {
            this.result = callable.call();
            latch.countDown();
        }
    }

}
