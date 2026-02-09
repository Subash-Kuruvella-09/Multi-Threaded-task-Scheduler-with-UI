package com.scheduler.task;

/**
 * Priority level for tasks.
 * Order is defined such that HIGH has the lowest ordinal value,
 * ensuring it comes first in a natural order PriorityQueue (Min-Heap).
 */
public enum Priority {
    HIGH,
    MEDIUM,
    LOW
}
