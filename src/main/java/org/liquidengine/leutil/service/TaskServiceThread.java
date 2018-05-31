package org.liquidengine.leutil.service;

import org.liquidengine.leutil.thread.NonManagedThread;

/**
 * Created by ShchAlexander on 21.09.2017.
 */
public class TaskServiceThread extends NonManagedThread {

    private TaskService taskService = new TaskService();

    /**
     * Instantiates a new Basic thread.
     *
     * @param threadName the thread name
     */
    public TaskServiceThread(String threadName) {
        super(threadName);
    }

    /**
     * Used to initialize.
     */
    @Override
    protected void initialize() {
    }

    /**
     * Update method which called every loop cycle.
     */
    @Override
    protected void update() {
        taskService.processTask();
    }

    /**
     * Used to destroy thread data.
     */
    @Override
    protected void destroy() {

    }

    public TaskService getTaskService() {
        return taskService;
    }
}
