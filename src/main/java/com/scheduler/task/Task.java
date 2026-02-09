package com.scheduler.task;

import java.util.UUID;

/**
 * A wrapper around Runnable that adds priority and scheduling capabilities.
 * Implements Comparable to allow sorting in a PriorityQueue.
 */
public class Task implements Comparable<Task> {

    // Unique identifier for the task
    private final UUID taskId;

    // The actual unit of work
    private final Runnable action;

    // Priority level (HIGH < MEDIUM < LOW in ordinal for sorting)
    private final Priority priority;

    // Execution time (could be delay or absolute time depending on usage)
    private final long executionTime;

    // Timestamp when the task was created
    private final long submissionTime;

    /**
     * Creates a new Task.
     * 
     * @param action        The runnable to be executed.
     * @param priority      The task priority.
     * @param executionTime The scheduled execution time or delay.
     */
    // Timestamp when the task actually started execution by a worker
    private long executionStartTime;

    public Task(Runnable action, Priority priority, long executionTime) {
        this.taskId = UUID.randomUUID();
        this.action = action;
        this.priority = priority;
        this.executionTime = executionTime;
        // Use nanoTime for better precision in sorting
        this.submissionTime = System.nanoTime();
    }

    public void setExecutionStartTime(long executionStartTime) {
        this.executionStartTime = executionStartTime;
    }

    public long getExecutionStartTime() {
        return executionStartTime;
    }

    public UUID getTaskId() {
        return taskId;
    }

    public Runnable getAction() {
        return action;
    }

    public Priority getPriority() {
        return priority;
    }

    public long getExecutionTime() {
        return executionTime;
    }

    public long getSubmissionTime() {
        return submissionTime;
    }

    @Override
    public int compareTo(Task other) {
        // 1. Compare Priority
        // High priority (lower ordinal) should come first.
        int priorityDiff = this.priority.compareTo(other.priority);
        if (priorityDiff != 0) {
            return priorityDiff;
        }

        // 2. Compare Submission Time (FIFO)
        // Earlier submission time comes first.
        return Long.compare(this.submissionTime, other.submissionTime);
    }

    @Override
    public String toString() {
        return "Task{" +
                "taskId=" + taskId +
                ", priority=" + priority +
                ", executionTime=" + executionTime +
                '}';
    }
}
