package org.liquidengine.leutil.thread;

/**
 * Non-Managed thread.
 *
 * @author Aliaksandr_Shcherbin.
 */
public abstract class NonManagedThread extends BasicThread {

    private long timer;

    private volatile int updates;
    private volatile int currentUps;

    /**
     * Instantiates a new Basic thread.
     *
     * @param threadName the thread name
     */
    public NonManagedThread(String threadName) {
        super(threadName);
    }

    /**
     * Initialize.
     */
    @Override
    void initializeThread() {
        initialize();
        timer = System.currentTimeMillis();
    }

    /**
     * Loop.
     */
    @Override
    void loopThread() {
        update();
        updates++;
        if (System.currentTimeMillis() - timer >= 1000) {
            timer += 1000;
            currentUps = updates;
            updates = 0;
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
     * Update method which called every loop cycle.
     */
    protected abstract void update();

    /**
     * Used to destroy thread data.
     */
    protected abstract void destroy();

    /**
     * Returns last actual ups.
     *
     * @return last actual ups.
     */
    public int getCurrentUps() {
        return currentUps;
    }
}
