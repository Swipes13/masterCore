package org.liquidengine.leutil.thread;

import java.util.concurrent.TimeUnit;

/**
 * Managed thread. Calls {@link #update()} specified times per second. There is no guarantees that {@link #update()} method would be called exactly specified
 * times per second.
 *
 * @author Aliaksandr_Shcherbin.
 */
public abstract class ManagedThread extends BasicThread {
    /**
     * Updates per second
     */
    protected int ups = 100;

    /**
     * Sleep time in nanoseconds
     */
    protected volatile long sleepTime = 10;

    /**
     * If this flag is <b>true</b> then if thread has free time it will sleep for provided time.
     */
    protected volatile boolean needToSleep;

    private long timer;
    private long last;
    private long delta;
    private double nanosPerUpdate;

    private volatile int updates = 100;
    private volatile int currentUps;

    /**
     * Instantiates a new Basic thread. <p> {@link #updates} by default is 100.
     *
     * @param threadName the thread name
     */
    public ManagedThread(String threadName) {
        super(threadName);
    }

    /**
     * Instantiates a new Basic thread.
     *
     * @param threadName thread name
     * @param ups expected updates per second
     */
    public ManagedThread(String threadName, int ups) {
        super(threadName);
        this.ups = ups;
    }

    /**
     * Instantiates a new Basic thread.
     *
     * @param threadName thread name
     * @param ups expected updates per second
     * @param sleepTime sleep time (time to sleep when idle)
     */
    public ManagedThread(String threadName, int ups, long sleepTime) {
        super(threadName);
        this.ups = ups;
        this.sleepTime = sleepTime;
    }

    /**
     * Initialize.
     */
    @Override
    void initializeThread() {
        initialize();
        timer = System.currentTimeMillis();
        last = System.nanoTime();
        delta = 0;
        nanosPerUpdate = 1_000_000_000 / ups;
    }

    /**
     * Loop.
     */
    @Override
    void loopThread() {
        long now = System.nanoTime();
        delta += now - last;
        last = now;
        if (isRunning() && delta >= nanosPerUpdate) {
            do {
                update();
                delta -= nanosPerUpdate;
                updates++;
            } while (isRunning() && delta >= nanosPerUpdate);
        } else {
            if (needToSleep) {
                sleep(sleepTime);
            }
        }
        if (System.currentTimeMillis() - timer >= 1000) {
            timer += 1000;
            currentUps = updates;
            updates = 0;
        }
    }

    /**
     * Used to sleep specified time when idle till next update.
     */
    private void sleep(long sleepTime) {
        try {
            TimeUnit.NANOSECONDS.sleep(sleepTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Destroy.
     */
    @Override
    void destroyThread() {
        destroy();
    }

    /**
     * Used to initialize.
     */
    protected abstract void initialize();

    /**
     * Update method which called {@link #ups} times per second.
     */
    protected abstract void update();

    /**
     * Used to destroy thread data.
     */
    protected abstract void destroy();

    /**
     * Returns true if thread sleeps when idle.
     *
     * @return true if thread sleeps when idle.
     */
    public boolean isNeedToSleep() {
        return needToSleep;
    }

    /**
     * Sets if thread sleeps when idle.
     *
     * @param needToSleep if thread sleeps when idle.
     */
    public void setNeedToSleep(boolean needToSleep) {
        this.needToSleep = needToSleep;
    }

    /**
     * Returns sleep time in nanoseconds.
     *
     * @return sleep time.
     */
    public long getSleepTime() {
        return sleepTime;
    }

    /**
     * Used to set sleep time in nanoseconds.
     *
     * @param sleepTime sleep time in nanoseconds.
     */
    public void setSleepTime(long sleepTime) {
        this.sleepTime = sleepTime;
    }

    /**
     * Returns updates per second (times to call {@link #update()} method). <p> There is no guarantees that {@link #update()} method would be called exactly
     * specified times per second.
     *
     * @return updates per second.
     */
    public int getUps() {
        return ups;
    }

    /**
     * Used to set updates per second (times to call {@link #update()} method). <p> There is no guarantees that {@link #update()} method would be called exactly
     * specified times per second.
     *
     * @param ups updates per second.
     */
    public void setUps(int ups) {
        this.ups = ups;
        nanosPerUpdate = 1_000_000_000 / ups;
    }

    /**
     * Returns last actual ups.
     *
     * @return last actual ups.
     */
    public int getCurrentUps() {
        return currentUps;
    }
}
