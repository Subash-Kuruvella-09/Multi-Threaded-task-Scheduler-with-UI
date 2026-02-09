@echo off
REM Compilation and execution script for Task Scheduler with Swing GUI
REM This script compiles all Java files and runs the GUI application

setlocal enabledelayedexpansion

REM Check if Java is installed
java -version >nul 2>&1
if errorlevel 1 (
    echo Error: Java is not installed or not in PATH
    pause
    exit /b 1
)

cd /d "%~dp0"

echo.
echo ========================================
echo Task Scheduler GUI - Build and Run
echo ========================================
echo.

REM Set source and output directories
set "SRC_DIR=src\main\java"
set "OUT_DIR=bin"

REM Create bin directory if it doesn't exist
if not exist "%OUT_DIR%" mkdir "%OUT_DIR%"

echo Compiling Java source files...
javac -d "%OUT_DIR%" "%SRC_DIR%\com\scheduler\task\Priority.java" 2>nul
javac -d "%OUT_DIR%" "%SRC_DIR%\com\scheduler\task\Task.java" 2>nul
javac -d "%OUT_DIR%" "%SRC_DIR%\com\scheduler\service\TaskScheduler.java" 2>nul
javac -d "%OUT_DIR%" "%SRC_DIR%\com\scheduler\ui\SchedulerMetrics.java" 2>nul
javac -d "%OUT_DIR%" "%SRC_DIR%\com\scheduler\ui\SchedulerGUI.java" 2>nul
javac -d "%OUT_DIR%" "%SRC_DIR%\com\scheduler\Simulation.java" 2>nul

if errorlevel 1 (
    echo.
    echo Compilation failed. Please check the errors above.
    pause
    exit /b 1
)

echo Compilation successful!
echo.
echo Starting GUI application...
echo.

REM Run the GUI application
java -cp "%OUT_DIR%" com.scheduler.ui.SchedulerGUI

endlocal
