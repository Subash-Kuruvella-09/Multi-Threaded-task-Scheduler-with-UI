# Quick Start Guide

## 60-Second Setup

### Windows Users

```bash
run-gui.bat
```

### Linux/Mac Users

```bash
chmod +x run-gui.sh
./run-gui.sh
```

That's it! The GUI will open automatically.

---

## 30-Second Tutorial

1. **Set Parameters**
   - Total Tasks: 100 (try different values 10-500)
   - Worker Threads: 4 (try 1, 2, 4, 8, 16)

2. **Click "Start Simulation"**

3. **Watch Real-Time Metrics**
   - Green progress bar shows completion
   - Metrics update every 0.5 seconds
   - Log shows simulation progress

4. **When Done**
   - Results display in log
   - Click "Start Simulation" to run again

---

## Key Metrics Explained

| Metric              | Meaning                | Good Value              |
| ------------------- | ---------------------- | ----------------------- |
| **Completed Tasks** | Progress tracker       | Should reach 100%       |
| **Queued Tasks**    | Waiting tasks in queue | Should be low           |
| **Avg Wait Time**   | Time to start (ms)     | Lower is better         |
| **Throughput**      | Tasks/second           | Higher is better        |
| **Progress Bar**    | Visual completion      | Will go from 0% to 100% |

---

## What to Try

### Experiment 1: Thread Scaling

- Run with 100 tasks, 1 thread → Note throughput
- Run with 100 tasks, 4 threads → Note throughput
- Run with 100 tasks, 8 threads → Note throughput

**Observation**: More threads = faster completion ⚡

### Experiment 2: Load Testing

- Run with 50 tasks, 2 threads
- Run with 500 tasks, 2 threads

**Observation**: More tasks = longer execution, higher queue size 📊

### Experiment 3: Priority Testing

- Run with 100 tasks (mixed priorities)
- Check if HIGH priority tasks complete first ✓

---

## Requirements

✓ Java 8 or higher (check: `java -version`)  
✓ Windows, Linux, or macOS  
✓ ~30 seconds to compile (automatic)  
✓ ~5-30 seconds to run simulation (depends on task count)

---

## Troubleshooting

| Problem           | Solution                                                   |
| ----------------- | ---------------------------------------------------------- |
| GUI won't start   | Check `java -version` returns 1.8.0+                       |
| Compilation error | Check all files in `src/main/java/` exist                  |
| GUI freezes       | Normal - simulation runs in background, wait or click Stop |
| Metrics show 0    | Wait 500ms for first update                                |

---

## Resume Highlight

**Perfect interview answer:**

> "I built an interactive GUI that visualizes my multi-threaded scheduler in real-time. It shows live metrics like task throughput, queue wait times, and completion progress. The GUI is thread-safe, updates every 500ms, and lets you test different configurations with 10-500 tasks across 1-16 threads. It demonstrates my understanding of concurrent programming, Java Swing, and system performance analysis."

---

## Next Steps

After running the GUI:

1. ✅ Test with different task/thread combinations
2. ✅ Note how metrics change with different loads
3. ✅ Talk about findings in interviews
4. ✅ Consider extending with more features (see SETUP.md)

---

**Ready? Run `run-gui.bat` or `./run-gui.sh` now! 🚀**
