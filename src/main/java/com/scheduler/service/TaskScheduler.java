package com.scheduler.service;

import com.scheduler.task.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A custom multi-threaded task scheduler.
 * Supports:
 * - Priority-based execution (High > Medium > Low).
 * - Delayed execution.
 * - Graceful shutdown.
 */
public class TaskScheduler {

    // Main queue for ready-to-execute tasks
    private final BlockingQueue<Task> taskQueue;

    // Holding area for delayed tasks
    private final DelayQueue<ScheduledTask> delayQueue;

    // Worker threads
    private final List<Thread> workers;

    // Poller thread to move tasks from delayQueue to taskQueue
    private final Thread delayDispatcher;

    private final AtomicBoolean isShutdown;

    /**
     * @param threadCount Number of worker threads to start.
     */
    public TaskScheduler(int threadCount) {
        this.taskQueue = new PriorityBlockingQueue<>();
        this.delayQueue = new DelayQueue<>();
        this.workers = new ArrayList<>(threadCount);
        this.isShutdown = new AtomicBoolean(false);

        // Initialize and start worker threads
        for (int i = 0; i < threadCount; i++) {
            Thread worker = new Thread(new Worker(), "Scheduler-Worker-" + i);
            workers.add(worker);
            worker.start();
        }

        // Initialize and start delay dispatcher
        this.delayDispatcher = new Thread(new DelayDispatcher(), "Delay-Dispatcher");
        this.delayDispatcher.start();
    }

    /**
     * Submits a task for execution.
     * 
     * @param task The task to run.
     * @throws IllegalStateException if the scheduler is shut down.
     */
    public void submit(Task task) {
        if (isShutdown.get()) {
            throw new IllegalStateException("Scheduler is shut down. Cannot accept new tasks.");
        }

        if (task.getExecutionTime() > 0) {
            // Task has a delay, put in DelayQueue
            delayQueue.offer(new ScheduledTask(task));
        } else {
            // No delay, ready to run directly
            taskQueue.offer(task);
        }
    }

    /**
     * Initiates a graceful shutdown.
     * New tasks will be rejected.
     * Existing tasks in the queue will be processed.
     * Idle workers will be interrupted to finish.
     */
    public void shutdown() {
        if (isShutdown.compareAndSet(false, true)) {
            // Interrupt the dispatcher to stop waiting for new delays (optional, implies we
            // stop processing future delays)
            // However, typical graceful shutdown might want to finish pending delays?
            // "Wait for the queue to empty" -> usually refers to ready queue.
            // We'll interrupt the dispatcher so it exits when delayQueue is empty or
            // immediately.
            delayDispatcher.interrupt();

            // Interrupt all workers to wake them up if they are idle (waiting on queue)
            for (Thread worker : workers) {
                worker.interrupt();
            }
        }
    }

    /**
     * Worker logic: fetch tasks from priority queue and execute them.
     */
    private class Worker implements Runnable {
        @Override
        public void run() {
            while (true) {
                // Shutdown condition: flag is set AND queue is empty.
                if (isShutdown.get() && taskQueue.isEmpty()) {
                    break;
                }

                try {
                    // Poll with timeout to periodically check shutdown status (or rely on
                    // interrupt)
                    Task task = taskQueue.poll(1, TimeUnit.SECONDS);
                    if (task != null) {
                        task.setExecutionStartTime(System.nanoTime());
                        task.getAction().run();
                    }
                } catch (InterruptedException e) {
                    // If interrupted, loop back to check condition.
                    // If shutdown is true, we will likely exit if queue is empty.
                    // Preserve interrupt status just in case, though we handle it by loop logic.
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                    System.err.println("Error executing task: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Poller logic: Move tasks from DelayQueue to Main Queue when ready.
     */
    private class DelayDispatcher implements Runnable {
        @Override
        public void run() {
            while (!isShutdown.get() || !delayQueue.isEmpty()) {
                try {
                    // Take blocks until an element is expired (ready)
                    ScheduledTask scheduled = delayQueue.poll(1, TimeUnit.SECONDS);

                    if (scheduled != null) {
                        if (isShutdown.get()) {
                            // If shutdown, decided if we still queue it.
                            // "Wait for queue to empty" usually implies processing what we have.
                            taskQueue.offer(scheduled.getTask());
                        } else {
                            taskQueue.offer(scheduled.getTask());
                        }
                    } else {
                        // If null (timeout) and shutdown, we can exit if delay queue is empty
                        if (isShutdown.get() && delayQueue.isEmpty())
                            break;
                    }
                } catch (InterruptedException e) {
                    // Interrupted during shutdown
                    if (isShutdown.get()) {
                        // Exit loop
                        break;
                    }
                }
            }
        }
    }

    /**
     * Wrapper for DelayQueue to handle timing.
     */
    private static class ScheduledTask implements Delayed {
        private final Task task;
        private final long triggerTime;

        public ScheduledTask(Task task) {
            this.task = task;
            // Convert delay duration to absolute trigger time
            this.triggerTime = System.currentTimeMillis() + task.getExecutionTime();
        }

        public Task getTask() {
            return task;
        }

        @Override
        public long getDelay(TimeUnit unit) {
            long diff = triggerTime - System.currentTimeMillis();
            return unit.convert(diff, TimeUnit.MILLISECONDS);
        }

        @Override
        public int compareTo(Delayed o) {
            if (this == o)
                return 0;
            long diff = this.getDelay(TimeUnit.MILLISECONDS) - o.getDelay(TimeUnit.MILLISECONDS);
            return Long.compare(diff, 0);
        }
    }
}
