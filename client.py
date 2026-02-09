import subprocess
import csv
import io
import sys

def run_simulation(num_tasks=50, num_threads=3):
    print(f"--- Running Simulation: Tasks={num_tasks}, Threads={num_threads} ---")
    
    # Classpath assumption: src/main/java is where the source is, 
    # but we compiled classes there implicitly or in root?
    # The previous javac command compiled typically into the same directories as .java files by default
    # unless -d was specified. The user did not specify -d.
    # So the classpath should be src/main/java.
    
    classpath = "src/main/java"
    cmd = ["java", "-cp", classpath, "com.scheduler.Simulation", str(num_tasks), str(num_threads)]
    
    try:
        result = subprocess.run(cmd, capture_output=True, text=True, check=True)
        return result.stdout
    except subprocess.CalledProcessError as e:
        print("Error running Java simulation:")
        print(e.stderr)
        return None

def analyze_metrics(csv_data):
    if not csv_data:
        return

    reader = csv.DictReader(io.StringIO(csv_data))
    
    total_wait_time = 0
    count = 0
    total_sim_time = 0
    
    rows = list(reader)
    if not rows:
        print("No data returned.")
        return

    for row in rows:
        total_wait_time += int(row['QueueWaitTime'])
        total_sim_time = int(row['TotalSimTimeMs']) # Constant for all rows in a run
        count += 1
    
    avg_wait_time_ns = total_wait_time / count if count > 0 else 0
    avg_wait_time_ms = avg_wait_time_ns / 1_000_000.0
    
    # Throughput (Tasks / Second)
    # TotalSimTime is in ms.
    throughput = (count / (total_sim_time / 1000.0)) if total_sim_time > 0 else 0
    
    print(f"\nResults:")
    print(f"Total Tasks Processed: {count}")
    print(f"Total Simulation Time: {total_sim_time} ms")
    print(f"Average Queue Wait Time: {avg_wait_time_ms:.4f} ms")
    print(f"Throughput: {throughput:.2f} tasks/sec")

if __name__ == "__main__":
    output = run_simulation()
    analyze_metrics(output)
