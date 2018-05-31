package org.liquidengine.leutil.thread;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * The type Basic thread.
 *
 * @author Aliaksandr_Shcherbin.
 */
public abstract class BasicThread implements Runnable {

    private final String threadName;

    private volatile boolean initialized = false;
    private AtomicBoolean initializing = new AtomicBoolean(false);
    private AtomicBoolean destroyed = new AtomicBoolean(false);
    private AtomicBoolean started = new AtomicBoolean(false);

    private volatile boolean alive = false;
    private volatile boolean running = false;
    private Thread thread;

    private volatile boolean failed;

    /**
     * Instantiates a new Basic thread.
     *
     * @param threadName the thread name
     */
    public BasicThread(String threadName) {
        this.threadName = threadName;
    }

    /**
     * Starts thread.
     */
    public final void start() {
        if (destroyed.get()) {
            throw new IllegalStateException("Thread cannot be started again.");
        }
        if (started.compareAndSet(false, true)) { // starting thread
            running = true;
            alive = true;
            thread = new Thread(this, threadName);
            thread.start();
        } else { // if thread already started
            throw new IllegalStateException("Thread cannot be started twice. Thread already started.");
        }
    }

    /**
     * Used by this thread.
     */
    public final void run() {
        try {
            innerInitialize();
            while (running) {
                loopThread();
            }
            innerDestroy();
        } catch (Throwable e) {
            if (!destroyed.get()) {
                try {
                    innerDestroy();
                } catch (Throwable ex) {
                    ex.printStackTrace();
                }
            }
            alive = false;
            failed = true;
            throw e;
        }
        alive = false;
    }

    /**
     * Used to initialize thread.
     */
    private void innerInitialize() {
        if (initializing.compareAndSet(false, true)) {
            initializeThread();
            initialized = true;
        }
    }

    /**
     * Used to destroy thread.
     */
    private void innerDestroy() {
        if (destroyed.compareAndSet(false, true)) {
            destroyThread();
        }
    }

    /**
     * Used to stop thread.
     */
    public void stop() {
        running = false;
    }

    /**
     * Initialize.
     */
    abstract void initializeThread();

    /**
     * Loop.
     */
    abstract void loopThread();

    /**
     * Destroy.
     */
    abstract void destroyThread();

    /**
     * Is thread alive.
     *
     * @return true if thread alive.
     */
    public final boolean isAlive() {
        return alive;
    }

    /**
     * Is thread failed.
     *
     * @return true if thread failed.
     */
    public final boolean isFailed() {
        return failed;
    }

    /**
     * Is thread destroyed.
     *
     * @return true if thread destroyed.
     */
    public final boolean isDestroyed() {
        return destroyed.get();
    }

    /**
     * Is thread initialized.
     *
     * @return true if thread initialized.
     */
    public boolean isInitialized() {
        return initialized;
    }

    /**
     * Is thread started.
     *
     * @return true if thread started.
     */
    public boolean isStarted() {
        return started.get();
    }

    /**
     * Gets thread name.
     *
     * @return the thread name
     */
    public String getThreadName() {
        return threadName;
    }

    /**
     * Is need to run boolean.
     *
     * @return the boolean
     */
    public boolean isRunning() {
        return running;
    }
}
