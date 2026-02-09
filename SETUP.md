# GUI Setup & Usage Guide

## Overview

Your Multi-Threaded Task Scheduler now includes an interactive **Swing-based GUI** that provides real-time visualization of task execution, performance metrics, and scheduling behavior.

## Quick Start

### Option 1: Run the GUI (Recommended)

**Windows:**

```bash
run-gui.bat
```

**Linux/macOS:**

```bash
chmod +x run-gui.sh
./run-gui.sh
```

The script will:

1. Compile all Java source files
2. Generate `.class` files in the `bin/` directory
3. Launch the GUI application

### Option 2: Manual Compilation & Run

```bash
# Create output directory
mkdir bin

# Compile all Java files
javac -d bin src/main/java/com/scheduler/task/*.java
javac -d bin src/main/java/com/scheduler/service/*.java
javac -d bin src/main/java/com/scheduler/ui/*.java
javac -d bin src/main/java/com/scheduler/Simulation.java

# Run GUI
java -cp bin com.scheduler.ui.SchedulerGUI
```

## GUI Features

### 1. **Configuration Panel** (Top)

- **Total Tasks**: Adjust number of tasks to simulate (10-500)
- **Worker Threads**: Set number of concurrent threads (1-16)
- **Start Button**: Begin simulation with current settings
- **Stop Button**: Halt running simulation (enabled during simulation)
- **Status Label**: Shows current operation status

### 2. **Real-Time Metrics** (Center-Left)

Displays live statistics that update every 500ms:

- **Completed Tasks**: Shows count of completed vs total (e.g., "42/100")
- **Queued Tasks**: Current number of tasks waiting in queue
- **Avg Wait Time**: Average time from task submission to execution (in ms)
- **Throughput**: Tasks per second execution rate
- **Priority Distribution**:
  - HIGH Priority Tasks: Count of high-priority tasks submitted
  - MEDIUM Priority Tasks: Count of medium-priority tasks submitted
  - LOW Priority Tasks: Count of low-priority tasks submitted
- **Overall Progress**: Visual progress bar (0-100%)

### 3. **Execution Log** (Bottom)

- Real-time log of simulation events
- Shows simulation start, completion, and performance analysis
- Auto-scrolls to latest log entries
- Key metrics printed at simulation end

## Usage Workflow

### Step 1: Configure Simulation

1. Open the GUI application
2. Set desired **Total Tasks** (e.g., 100)
3. Set desired **Worker Threads** (e.g., 4)

### Step 2: Start Simulation

1. Click the **Start Simulation** button
2. Status label will show "Initializing scheduler..."
3. Watch metrics update in real-time

### Step 3: Monitor Execution

- **Real-time Metrics** panel updates every 500ms
- **Execution Log** shows progress
- **Progress Bar** fills as tasks complete

### Step 4: Analyze Results

When simulation completes, the log displays:

- Total execution time (ms)
- Average queue wait time (ms)
- Task throughput (tasks/sec)

### Step 5: Run Another Simulation

- Click **Start Simulation** again
- Metrics reset automatically
- Configure different parameters if desired

## What Each Metric Means

### Completed Tasks (e.g., "42/100")

- Shows how many tasks have finished
- Updates as tasks complete
- Reaches 100% when simulation finishes

### Queued Tasks

- Number of tasks currently waiting in the priority queue
- Lower is better (indicates efficient task processing)
- Should drop to 0 at end of simulation

### Average Wait Time (ms)

- Time from task submission to execution start
- Lower values indicate responsive scheduling
- Metric: (sum of all wait times) / (number of tasks)

### Throughput (tasks/sec)

- How many tasks complete per second
- Increases as more threads process tasks
- Formula: (completed tasks × 1000) / total_time_ms

### Priority Distribution

- Shows balance across different priority levels
- Useful for testing fairness in scheduling
- HIGH tasks should execute before MEDIUM/LOW when present

### Progress Bar

- Visual representation of completion percentage
- Updates continuously during simulation
- Reaches 100% when all tasks complete

## Example Scenarios

### Scenario 1: Testing Thread Scaling

**Question**: How does adding more threads affect performance?

**Experiment**:

1. Run with 100 tasks, 1 thread
2. Note the throughput and average wait time
3. Run again with 2, 4, 8 threads
4. Compare metrics

**Expected Results**: More threads → higher throughput, lower wait times

### Scenario 2: Testing Priority Fairness

**Question**: Do HIGH priority tasks get faster execution?

**Experiment**:

1. Run with 100 tasks spread across priorities
2. Note the average wait times for each priority
3. Observe in log if HIGH priority tasks complete first

**Expected Results**: HIGH priority tasks should complete sooner on average

### Scenario 3: Queue Load Testing

**Question**: How does queue size grow under heavy load?

**Experiment**:

1. Run with 500 tasks, 2 threads (heavy load)
2. Watch the "Queued Tasks" metric
3. Increase to 4 threads in next run
4. Compare queue sizes

**Expected Results**: More threads → smaller queues

## Technical Details

### Architecture Changes

**New Classes**:

- `SchedulerMetrics` (`src/main/java/com/scheduler/ui/SchedulerMetrics.java`)
  - Thread-safe metrics collection
  - Uses `AtomicInteger` and `AtomicLong` for thread-safety
  - Calculates derived metrics (average wait time, throughput)

- `SchedulerGUI` (`src/main/java/com/scheduler/ui/SchedulerGUI.java`)
  - Swing-based GUI application
  - Launches simulations in background threads
  - Updates UI via SwingUtilities for thread-safety

### Thread Safety

- GUI runs on EDT (Event Dispatch Thread)
- Simulation runs in background thread
- Metrics uses atomic variables
- UI updates marshalled through `SwingUtilities.invokeLater()`

### Update Frequency

- Metrics refresh: Every 500ms
- Log updates: Real-time (or as added)
- Thread-safe because SchedulerMetrics uses atomic operations

## Troubleshooting

### Issue: GUI doesn't start

**Solution**: Ensure Java is installed and in PATH

```bash
java -version
javac -version
```

### Issue: "Compilation failed" error

**Solution**: Check Java version (requires Java 8+)

```bash
java -version  # Should be 1.8.0 or higher
```

### Issue: GUI freezes during simulation

**Solution**: This is normal - GUI is responsive, but metrics update every 500ms

- Wait for simulation to complete
- Or click Stop button to cancel

### Issue: Metrics show 0 for everything

**Solution**: Wait for first metrics update (500ms after start)

- Or simulation may still be initializing
- Check status label for current operation

## Performance Tips

### For Faster Simulations

- Reduce total tasks (e.g., 50 instead of 100)
- This makes simulation complete faster for testing

### For More Accurate Metrics

- Use more tasks (e.g., 200-500)
- More tasks provide better statistical averaging

### For Better Threading Tests

- Keep task count constant
- Vary thread count (1-16)
- Compare metrics across runs

## Resume Talking Points

When explaining this GUI to interviewers, highlight:

1. **GUI Development**:
   - "Developed interactive Swing GUI for real-time visualization"
   - "Implements thread-safe metrics collection"

2. **Performance Analysis**:
   - "Created metrics system to analyze scheduler efficiency"
   - "Measures throughput, latency, and fairness"

3. **User Experience**:
   - "Built responsive UI that updates without blocking simulation"
   - "Allows configurable parameters for flexible testing"

4. **System Design**:
   - "Separated concerns: scheduler logic, metrics, UI"
   - "Thread-safe design using atomic variables"

## File Structure

```
Project Root/
├── README.md                    # Project overview
├── SETUP.md                     # This file
├── run-gui.bat                  # Windows startup script
├── run-gui.sh                   # Linux/Mac startup script
├── client.py                    # Python simulation client
│
└── src/main/java/com/scheduler/
    ├── task/
    │   ├── Priority.java        # Priority enum
    │   └── Task.java            # Task class
    │
    ├── service/
    │   └── TaskScheduler.java   # Core scheduler
    │
    ├── ui/
    │   ├── SchedulerGUI.java    # Swing GUI application
    │   └── SchedulerMetrics.java # Real-time metrics
    │
    └── Simulation.java          # Console simulator
```

---

**Ready to run?** Execute `run-gui.bat` (Windows) or `./run-gui.sh` (Linux/Mac) to start!
