package com.scheduler;

import com.scheduler.service.TaskScheduler;
import com.scheduler.task.Priority;
import com.scheduler.task.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Main simulation entry point for the Task Scheduler.
 *
 * Usage:
 * java com.scheduler.Simulation <numTasks> <numThreads>
 *
 * Output:
 * CSV format to stdout:
 * TaskID,Priority,SubmissionTime(ns),ExecutionStartTime(ns),QueueWaitTime(ns)
 */
public class Simulation {
    public static void main(String[] args) throws InterruptedException {
        int numTasks = 100;
        int numThreads = 5;

        if (args.length >= 2) {
            numTasks = Integer.parseInt(args[0]);
            numThreads = Integer.parseInt(args[1]);
        }

        TaskScheduler scheduler = new TaskScheduler(numThreads);
        CountDownLatch latch = new CountDownLatch(numTasks);
        List<Task> completedTasks = new ArrayList<>();

        // Use a synchronized list or just collect metrics inside the task action if
        // careful.
        // For simplicity, we can just print metrics as tasks finish, OR collect them.
        // Let's collect them in a thread-safe way?
        // Actually, ArrayList is not thread safe.
        // We will just print to stdout directly from the task?
        // No, interleaved output is bad for CSV.
        // Let's use a concurrent queue or synchronized wrapper.
        List<Task> stats = java.util.Collections.synchronizedList(new ArrayList<>());

        Random rand = new Random();

        // Start time of simulation
        long simStart = System.currentTimeMillis();

        for (int i = 0; i < numTasks; i++) {
            Priority prio = Priority.values()[rand.nextInt(Priority.values().length)];
            // Random delay between 0 and 100ms
            long delay = rand.nextInt(100);

            Task task = new Task(() -> {
                try {
                    // Simulate work
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    latch.countDown();
                }
            }, prio, delay);

            // Add to stats list *after* execution?
            // The Task object is modified by the worker *before* execution.
            // So we can capture it here, but we need to know when it *finishes* to read the
            // updated field safely?
            // Actually, we are passing the task object itself.
            // We can add it to our list of 'all submitted tasks' and read them after the
            // latch.
            stats.add(task);

            scheduler.submit(task);
        }

        // Wait for all tasks to complete
        latch.await();

        long simEnd = System.currentTimeMillis();
        long totalTimeMs = simEnd - simStart;

        scheduler.shutdown();

        // Print Metrics Header
        System.out.println("TaskID,Priority,SubmissionTime,ExecutionStartTime,QueueWaitTime,TotalSimTimeMs,TotalTasks");

        // Print Metrics Data
        // To be safer, we should filter out tasks that might not have started if
        // something went wrong,
        // but with latch we know they finished.

        for (Task t : stats) {
            long waitTime = t.getExecutionStartTime() - t.getSubmissionTime();
            System.out.println(String.format("%s,%s,%d,%d,%d,%d,%d",
                    t.getTaskId(),
                    t.getPriority(),
                    t.getSubmissionTime(),
                    t.getExecutionStartTime(),
                    waitTime,
                    totalTimeMs,
                    numTasks));
        }
    }
}
