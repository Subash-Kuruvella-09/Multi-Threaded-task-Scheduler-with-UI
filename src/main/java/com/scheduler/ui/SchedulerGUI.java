package com.scheduler.ui;

import com.scheduler.service.TaskScheduler;
import com.scheduler.task.Priority;
import com.scheduler.task.Task;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

/**
 * Swing-based GUI for visualizing the multi-threaded task scheduler in
 * real-time.
 * Allows users to configure and run simulations with live metrics display.
 */
public class SchedulerGUI extends JFrame {
    private SchedulerMetrics metrics;
    private TaskScheduler scheduler;
    private volatile boolean isRunning = false;

    // UI Components
    private JSpinner tasksSpinner;
    private JSpinner threadsSpinner;
    private JButton startButton;
    private JButton stopButton;
    private JLabel statusLabel;
    private JLabel completedLabel;
    private JLabel queueLabel;
    private JLabel avgWaitLabel;
    private JLabel throughputLabel;
    private JLabel highPriorityLabel;
    private JLabel mediumPriorityLabel;
    private JLabel lowPriorityLabel;
    private JProgressBar overallProgress;
    private JTextArea logArea;

    public SchedulerGUI() {
        setTitle("Multi-Threaded Task Scheduler - Visualizer");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);

        metrics = new SchedulerMetrics();

        // Create main layout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Top: Control Panel
        mainPanel.add(createControlPanel(), BorderLayout.NORTH);

        // Center: Metrics Panel
        mainPanel.add(createMetricsPanel(), BorderLayout.CENTER);

        // Bottom: Log Panel
        mainPanel.add(createLogPanel(), BorderLayout.SOUTH);

        add(mainPanel);

        // Start metrics update thread
        startMetricsUpdateThread();

        setVisible(true);
    }

    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Configuration"));

        JLabel titleLabel = new JLabel("Task Scheduler Configuration");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));

        JPanel configBox = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));

        // Number of Tasks
        JPanel tasksBox = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        tasksBox.setBorder(BorderFactory.createTitledBorder("Total Tasks"));
        tasksSpinner = new JSpinner(new SpinnerNumberModel(100, 10, 500, 10));
        tasksSpinner.setPreferredSize(new Dimension(80, 30));
        tasksBox.add(tasksSpinner);

        // Number of Threads
        JPanel threadsBox = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        threadsBox.setBorder(BorderFactory.createTitledBorder("Worker Threads"));
        threadsSpinner = new JSpinner(new SpinnerNumberModel(4, 1, 16, 1));
        threadsSpinner.setPreferredSize(new Dimension(80, 30));
        threadsBox.add(threadsSpinner);

        // Buttons
        startButton = new JButton("Start Simulation");
        startButton.setPreferredSize(new Dimension(150, 40));
        startButton.setFont(new Font("Arial", Font.PLAIN, 12));
        startButton.addActionListener(e -> runSimulation());

        stopButton = new JButton("Stop");
        stopButton.setPreferredSize(new Dimension(150, 40));
        stopButton.setFont(new Font("Arial", Font.PLAIN, 12));
        stopButton.setEnabled(false);
        stopButton.addActionListener(e -> stopSimulation());

        configBox.add(tasksBox);
        configBox.add(threadsBox);
        configBox.add(startButton);
        configBox.add(stopButton);

        statusLabel = new JLabel("Ready");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        statusLabel.setForeground(new Color(0, 102, 204));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(configBox, BorderLayout.NORTH);
        topPanel.add(statusLabel, BorderLayout.SOUTH);

        panel.add(topPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createMetricsPanel() {
        JPanel mainPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        mainPanel.setBorder(BorderFactory.createTitledBorder("Real-Time Metrics"));

        // Left: Text Metrics
        JPanel leftPanel = new JPanel(new GridLayout(8, 1, 5, 5));
        leftPanel.setBorder(BorderFactory.createTitledBorder("Statistics"));

        // Completed Tasks
        JPanel completedPanel = createMetricPanel("Completed Tasks");
        completedLabel = (JLabel) completedPanel.getComponent(1);
        leftPanel.add(completedPanel);

        // Queued Tasks
        JPanel queuePanel = createMetricPanel("Queued Tasks");
        queueLabel = (JLabel) queuePanel.getComponent(1);
        leftPanel.add(queuePanel);

        // Average Wait Time
        JPanel waitPanel = createMetricPanel("Avg Wait Time");
        avgWaitLabel = (JLabel) waitPanel.getComponent(1);
        leftPanel.add(waitPanel);

        // Throughput
        JPanel throughputPanel = createMetricPanel("Throughput");
        throughputLabel = (JLabel) throughputPanel.getComponent(1);
        leftPanel.add(throughputPanel);

        // HIGH Priority
        JPanel highPanel = createMetricPanel("HIGH Priority Tasks");
        highPriorityLabel = (JLabel) highPanel.getComponent(1);
        leftPanel.add(highPanel);

        // MEDIUM Priority
        JPanel mediumPanel = createMetricPanel("MEDIUM Priority Tasks");
        mediumPriorityLabel = (JLabel) mediumPanel.getComponent(1);
        leftPanel.add(mediumPanel);

        // LOW Priority
        JPanel lowPanel = createMetricPanel("LOW Priority Tasks");
        lowPriorityLabel = (JLabel) lowPanel.getComponent(1);
        leftPanel.add(lowPanel);

        // Progress
        JPanel progressPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel progressLabel = new JLabel("Overall Progress:");
        overallProgress = new JProgressBar(0, 100);
        overallProgress.setStringPainted(true);
        overallProgress.setPreferredSize(new Dimension(200, 25));
        progressPanel.add(progressLabel);
        progressPanel.add(overallProgress);
        leftPanel.add(progressPanel);

        mainPanel.add(leftPanel);

        return mainPanel;
    }

    private JPanel createMetricPanel(String label) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        JLabel titleLabel = new JLabel(label + ":");
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        titleLabel.setPreferredSize(new Dimension(180, 20));

        JLabel valueLabel = new JLabel("0");
        valueLabel.setFont(new Font("Arial", Font.BOLD, 13));
        valueLabel.setForeground(new Color(0, 102, 204));
        valueLabel.setPreferredSize(new Dimension(100, 20));

        panel.add(titleLabel);
        panel.add(valueLabel);

        return panel;
    }

    private JPanel createLogPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Execution Log"));
        panel.setPreferredSize(new Dimension(0, 150));

        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Courier New", Font.PLAIN, 10));
        logArea.setLineWrap(true);
        logArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private synchronized void runSimulation() {
        if (isRunning) {
            JOptionPane.showMessageDialog(this, "Simulation already running!", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        isRunning = true;
        startButton.setEnabled(false);
        stopButton.setEnabled(true);
        tasksSpinner.setEnabled(false);
        threadsSpinner.setEnabled(false);

        metrics.reset();
        logArea.setText("");

        int numTasks = (Integer) tasksSpinner.getValue();
        int numThreads = (Integer) threadsSpinner.getValue();

        updateStatus("Initializing scheduler with " + numThreads + " threads...");
        addLog("Starting simulation with " + numTasks + " tasks and " + numThreads + " threads\n");

        // Run simulation in background thread
        Thread simulationThread = new Thread(() -> {
            try {
                executeSimulation(numTasks, numThreads);
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    addLog("ERROR: " + e.getMessage() + "\n");
                    resetUI();
                });
            }
        });
        simulationThread.setDaemon(true);
        simulationThread.start();
    }

    private void executeSimulation(int numTasks, int numThreads) throws InterruptedException {
        scheduler = new TaskScheduler(numThreads);
        CountDownLatch latch = new CountDownLatch(numTasks);
        List<Task> allTasks = new ArrayList<>();
        Random rand = new Random();

        metrics.setTotalTasks(numTasks);
        metrics.setStartTime(System.currentTimeMillis());

        updateStatus("Submitting " + numTasks + " tasks...");

        // Submit tasks
        for (int i = 0; i < numTasks && isRunning; i++) {
            Priority priority = Priority.values()[rand.nextInt(Priority.values().length)];
            long delay = rand.nextInt(100);

            Task task = new Task(() -> {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    metrics.incrementCompletedTasks();
                    latch.countDown();
                }
            }, priority, delay);

            allTasks.add(task);
            metrics.incrementPriority(priority.toString());
            scheduler.submit(task);
        }

        if (!isRunning) {
            scheduler.shutdown();
            return;
        }

        updateStatus("Waiting for " + numTasks + " tasks to complete...");

        // Wait for completion
        latch.await();

        metrics.setEndTime(System.currentTimeMillis());

        // Calculate final metrics
        for (Task t : allTasks) {
            long waitTime = t.getExecutionStartTime() - t.getSubmissionTime();
            metrics.addWaitTime(waitTime);
        }

        scheduler.shutdown();

        SwingUtilities.invokeLater(() -> {
            addLog("\nSimulation completed!\n");
            addLog("Total time: " + metrics.getElapsedTimeMs() + " ms\n");
            addLog("Average wait time: " + String.format("%.2f", metrics.getAverageWaitTimeMs()) + " ms\n");
            addLog("Throughput: " + String.format("%.2f", metrics.getThroughputTasksPerSecond()) + " tasks/sec\n");
            updateStatus("Simulation complete!");
            resetUI();
        });
    }

    private synchronized void stopSimulation() {
        isRunning = false;
        if (scheduler != null) {
            scheduler.shutdown();
        }
        updateStatus("Simulation stopped by user");
        resetUI();
    }

    private void startMetricsUpdateThread() {
        Thread metricsThread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(500);
                    updateMetricsUI();
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        metricsThread.setDaemon(true);
        metricsThread.start();
    }

    private void updateMetricsUI() {
        int completed = metrics.getCompletedTasks();
        int total = metrics.getTotalTasks();

        completedLabel.setText(completed + "/" + total);
        queueLabel.setText(String.valueOf(metrics.getQueuedTasks()));
        avgWaitLabel.setText(String.format("%.2f ms", metrics.getAverageWaitTimeMs()));
        throughputLabel.setText(String.format("%.2f tasks/sec", metrics.getThroughputTasksPerSecond()));
        highPriorityLabel.setText(String.valueOf(metrics.getHighPriorityTasks()));
        mediumPriorityLabel.setText(String.valueOf(metrics.getMediumPriorityTasks()));
        lowPriorityLabel.setText(String.valueOf(metrics.getLowPriorityTasks()));

        if (total > 0) {
            overallProgress.setValue((int) ((double) completed / total * 100));
        }
    }

    private void updateStatus(String message) {
        SwingUtilities.invokeLater(() -> statusLabel.setText(message));
    }

    private void addLog(String message) {
        SwingUtilities.invokeLater(() -> {
            logArea.append(message);
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }

    private void resetUI() {
        startButton.setEnabled(true);
        stopButton.setEnabled(false);
        tasksSpinner.setEnabled(true);
        threadsSpinner.setEnabled(true);
        isRunning = false;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SchedulerGUI::new);
    }
}
