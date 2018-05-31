package org.liquidengine.leutil.service;

import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by ShchAlexander on 21.09.2017.
 */
public class TaskService {

    private Queue<Task> tasks = new ConcurrentLinkedQueue<>();

    public void addTask(Task task) {
        tasks.add(task);
    }

    public <T> void createTaskNoWait(Callable<T> callable) {
        Task<T> task = new Task<>(callable);
        tasks.add(task);
    }

    public <T> T createTaskAndGet(Callable<T> callable) {
        Task<T> task = new Task<>(callable);
        tasks.add(task);
        try {
            return task.get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> Task<T> createTask(Callable<T> callable) {
        Task<T> task = new Task<>(callable);
        tasks.add(task);
        return task;
    }

    public void processTask() {
        Task task = tasks.poll();
        if (task != null) {
            try {
                task.execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
