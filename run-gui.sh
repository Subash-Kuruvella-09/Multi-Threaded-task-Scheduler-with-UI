#!/bin/bash
# Compilation and execution script for Task Scheduler with Swing GUI
# This script compiles all Java files and runs the GUI application

echo ""
echo "========================================"
echo "Task Scheduler GUI - Build & Run"
echo "========================================"
echo ""

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "Error: Java is not installed or not in PATH"
    exit 1
fi

# Set source and output directories
SRC_DIR="src/main/java"
OUT_DIR="bin"

# Create bin directory if it doesn't exist
mkdir -p "$OUT_DIR"

echo "Compiling Java source files..."
javac -d "$OUT_DIR" "$SRC_DIR/com/scheduler/task/Priority.java"
javac -d "$OUT_DIR" "$SRC_DIR/com/scheduler/task/Task.java"
javac -d "$OUT_DIR" "$SRC_DIR/com/scheduler/service/TaskScheduler.java"
javac -d "$OUT_DIR" "$SRC_DIR/com/scheduler/ui/SchedulerMetrics.java"
javac -d "$OUT_DIR" "$SRC_DIR/com/scheduler/ui/SchedulerGUI.java"
javac -d "$OUT_DIR" "$SRC_DIR/com/scheduler/Simulation.java"

if [ $? -ne 0 ]; then
    echo ""
    echo "Compilation failed. Please check the errors above."
    exit 1
fi

echo "Compilation successful!"
echo ""
echo "Starting GUI application..."
echo ""

# Run the GUI application
java -cp "$OUT_DIR" com.scheduler.ui.SchedulerGUI
