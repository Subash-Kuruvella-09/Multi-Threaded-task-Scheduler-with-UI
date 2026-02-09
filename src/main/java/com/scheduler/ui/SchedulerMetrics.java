package com.scheduler.ui;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Real-time metrics collection for the task scheduler.
 * Thread-safe container for scheduler statistics.
 */
public class SchedulerMetrics {
    private final AtomicInteger totalTasks = new AtomicInteger(0);
    private final AtomicInteger completedTasks = new AtomicInteger(0);
    private final AtomicInteger queuedTasks = new AtomicInteger(0);
    private final AtomicInteger runningTasks = new AtomicInteger(0);
    private final AtomicInteger highPriorityTasks = new AtomicInteger(0);
    private final AtomicInteger mediumPriorityTasks = new AtomicInteger(0);
    private final AtomicInteger lowPriorityTasks = new AtomicInteger(0);
    private final AtomicLong totalWaitTime = new AtomicLong(0);
    private final AtomicLong startTime = new AtomicLong(0);
    private final AtomicLong endTime = new AtomicLong(0);

    public void setTotalTasks(int count) {
        this.totalTasks.set(count);
    }

    public void incrementCompletedTasks() {
        this.completedTasks.incrementAndGet();
    }

    public void setQueuedTasks(int count) {
        this.queuedTasks.set(count);
    }

    public void setRunningTasks(int count) {
        this.runningTasks.set(count);
    }

    public void addWaitTime(long waitTimeNs) {
        this.totalWaitTime.addAndGet(waitTimeNs);
    }

    public void incrementPriority(String priority) {
        switch (priority.toUpperCase()) {
            case "HIGH":
                highPriorityTasks.incrementAndGet();
                break;
            case "MEDIUM":
                mediumPriorityTasks.incrementAndGet();
                break;
            case "LOW":
                lowPriorityTasks.incrementAndGet();
                break;
        }
    }

    public void setStartTime(long time) {
        this.startTime.set(time);
    }

    public void setEndTime(long time) {
        this.endTime.set(time);
    }

    // Getters
    public int getTotalTasks() {
        return totalTasks.get();
    }

    public int getCompletedTasks() {
        return completedTasks.get();
    }

    public int getQueuedTasks() {
        return queuedTasks.get();
    }

    public int getRunningTasks() {
        return runningTasks.get();
    }

    public int getHighPriorityTasks() {
        return highPriorityTasks.get();
    }

    public int getMediumPriorityTasks() {
        return mediumPriorityTasks.get();
    }

    public int getLowPriorityTasks() {
        return lowPriorityTasks.get();
    }

    public long getTotalWaitTime() {
        return totalWaitTime.get();
    }

    public double getAverageWaitTimeMs() {
        int completed = completedTasks.get();
        if (completed == 0)
            return 0;
        return (totalWaitTime.get() / 1_000_000.0) / completed;
    }

    public long getElapsedTimeMs() {
        long start = startTime.get();
        long end = endTime.get();
        if (start == 0)
            return 0;
        if (end == 0)
            return System.currentTimeMillis() - start;
        return end - start;
    }

    public double getThroughputTasksPerSecond() {
        long elapsed = getElapsedTimeMs();
        if (elapsed == 0)
            return 0;
        return (completedTasks.get() * 1000.0) / elapsed;
    }

    public void reset() {
        totalTasks.set(0);
        completedTasks.set(0);
        queuedTasks.set(0);
        runningTasks.set(0);
        totalWaitTime.set(0);
        highPriorityTasks.set(0);
        mediumPriorityTasks.set(0);
        lowPriorityTasks.set(0);
        startTime.set(0);
        endTime.set(0);
    }
}
