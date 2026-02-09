# Multi-Threaded Task Scheduler

A sophisticated Java-based multi-threaded task scheduler with real-time visualization GUI. Built to demonstrate practical understanding of concurrent programming, thread management, priority-based scheduling, and system performance analysis.

## Features

### Core Scheduler

- **Priority-Based Scheduling**: Tasks are executed based on priority (HIGH > MEDIUM > LOW)
- **Multi-threaded Execution**: Configurable worker threads for parallel task execution
- **Delayed Task Support**: Schedule tasks with delays
- **Thread-Safe Operations**: Complete synchronization using BlockingQueue and DelayQueue
- **Graceful Shutdown**: Clean shutdown with task completion guarantee

### Interactive GUI (NEW ⭐)

- **Real-Time Visualization**: Watch tasks execute in real-time
- **Live Metrics Dashboard**:
  - Completed vs Total tasks
  - Queue status
  - Average wait time
  - Task throughput (tasks/sec)
- **Priority Distribution Chart**: Visual representation of tasks by priority level
- **Configurable Parameters**: Adjust task count and thread count before running
- **Execution Log**: Real-time log of simulation progress

## Architecture

```
src/main/java/com/scheduler/
├── task/
│   ├── Priority.java         # Priority enum (HIGH, MEDIUM, LOW)
│   └── Task.java             # Task class with priority & timing
├── service/
│   └── TaskScheduler.java    # Core scheduler with worker threads
├── ui/
│   ├── SchedulerGUI.java     # JavaFX GUI application
│   └── SchedulerMetrics.java # Real-time metrics collection
└── Simulation.java           # Console-based simulator
```

## Quick Start

### Option 1: Interactive GUI (Recommended) 🎨

**Windows:**

```bash
run-gui.bat
```

**Linux/Mac:**

```bash
chmod +x run-gui.sh
./run-gui.sh
```

The GUI will open with interactive controls:

1. Set **Total Tasks** (10-500)
2. Set **Worker Threads** (1-16)
3. Click **▶ Start Simulation**
4. Watch real-time metrics and charts
5. Click **⏹ Stop** to halt execution

### Option 2: Command-Line (Console Output) 💻

Compile and run the simulator:

```bash
javac -d bin src/main/java/com/scheduler/task/*.java
javac -d bin src/main/java/com/scheduler/service/*.java
javac -d bin src/main/java/com/scheduler/Simulation.java

java -cp bin com.scheduler.Simulation 100 4
```

Or use the Python client:

```bash
python client.py
```

## Key Concepts Demonstrated

### 1. **Thread Management**

- ThreadPool pattern with fixed worker threads
- Thread lifecycle management
- Graceful shutdown with CountDownLatch

### 2. **Concurrency Control**

- `PriorityBlockingQueue` for priority ordering
- `DelayQueue` for delayed task execution
- `AtomicBoolean` and `AtomicLong` for thread-safe counters
- Synchronized collections for metrics

### 3. **Task Scheduling**

- Priority comparator implementation
- FIFO within same priority level
- Delay-based scheduling with dispatcher thread

### 4. **Performance Metrics**

- Queue wait time (submission → execution)
- Total execution time
- Task throughput calculation
- Average wait time analysis

## Performance Characteristics

The scheduler demonstrates excellent performance with:

- **Scalability**: Tested with up to 500 tasks across 1-16 threads
- **Low Latency**: Precise timing measurements in nanoseconds
- **High Throughput**: Efficient task distribution across workers
- **Fair Scheduling**: Priority-based with FIFO fairness

## Resume Highlights

This project showcases expertise in:

- ✅ **Concurrent Programming**: Multi-threaded architecture
- ✅ **Data Structures**: Priority queues, delay queues
- ✅ **Synchronization**: Thread-safe operations
- ✅ **GUI Development**: JavaFX visualization
- ✅ **Performance Analysis**: Real-time metrics collection
- ✅ **System Design**: Scalable scheduler architecture
- ✅ **Software Engineering**: Clean code, documentation, testing

Perfect for interviews with **AMD, Intel, Google, Microsoft** - companies that value deep systems knowledge!

## Technology Stack

- **Language**: Java 8+
- **Concurrency**: Java Concurrent Framework (java.util.concurrent)
- **GUI**: JavaFX
- **Python Integration**: subprocess for simulation execution

## Customization & Extension

### Add New Priority Levels

Edit `src/main/java/com/scheduler/task/Priority.java`:

```java
public enum Priority {
    CRITICAL,  // New level
    HIGH,
    MEDIUM,
    LOW,
    BACKGROUND // New level
}
```

### Change Task Execution Time

Edit `src/main/java/com/scheduler/Simulation.java`:

```java
Thread.sleep(10);  // Change execution time in milliseconds
```

### Implement Different Scheduling Algorithms

Create alternative implementations in the `service/` package:

- Shortest Job First (SJF)
- Round Robin
- Multi-level Feedback Queue

## What Makes This Project Stand Out

1. **Complete Solution**: Not just a basic scheduler, but includes GUI for visualization
2. **Production-Ready Code**: Proper error handling, thread safety, resource cleanup
3. **Rich Metrics**: Real-time performance monitoring and analysis
4. **Professional UI**: Modern JavaFX interface with live charts
5. **Well-Documented**: Clear comments and comprehensive README
6. **Extensible Design**: Easy to add new features and algorithms

## Interview Talking Points

- How would you scale this to handle millions of tasks?
- How does priority-based scheduling affect task fairness?
- What are the benefits of using BlockingQueue over synchronization?
- How would you handle task failures and retries?
- What scheduling algorithm would you use for real-time systems?

---

**Built with ❤️ for systems programming enthusiasts and aspiring software engineers**
