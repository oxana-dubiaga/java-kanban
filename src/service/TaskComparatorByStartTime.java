package service;

import model.Task;

import java.util.Comparator;

public class TaskComparatorByStartTime implements Comparator<Task> {
    @Override
    public int compare(Task t1, Task t2) {
        if (t1.getStartTime().isBefore(t2.getStartTime())) {
            return -1;
        } else if (t1.getStartTime().isAfter(t2.getStartTime())) {
            return 1;
        } else return 0;
    }
}

