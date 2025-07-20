package model;

import service.NoStartOrEndTimeException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.TreeSet;

public class Epic extends Task {

    private HashSet<Integer> subtasksIds;
    private LocalDateTime endTime;
    private TreeSet<LocalDateTime> startTimes = new TreeSet<>();
    private TreeSet<LocalDateTime> endTimes = new TreeSet<>();

    public Epic(String name, String discription, int id) {
        super(name, discription, id);
        subtasksIds = new HashSet<>();
        endTime = null;
        startTime = null;
        duration = Duration.ofMinutes(0);
    }

    @Override
    public LocalDateTime getStartTime() throws NoStartOrEndTimeException {
        if (startTime != null) {
            return startTime;
        }
        throw new NoStartOrEndTimeException("В эпике нет ни одной подзадачи, поэтому время старта отсутствует");
    }

    @Override
    public LocalDateTime getEndTime() throws NoStartOrEndTimeException {
        if (endTime != null) {
            return endTime;
        }
        throw new NoStartOrEndTimeException("В эпике нет ни одной подзадачи, поэтому время окончания отсутствует");
    }


    public void addSubtask(Subtask subtask) {
        int id = subtask.getId();
        if (subtasksIds.contains(id)) {
            System.out.println("Такая подзадача уже есть в данном эпике");
        } else {
            subtasksIds.add(id);
            startTimes.add(subtask.getStartTime());
            endTimes.add(subtask.getEndTime());
            startTime = startTimes.first();
            endTime = endTimes.last();
            duration = Duration.between(startTime, endTime);
        }
    }

    public void deleteSubtask(Subtask subtask) {
        int id = subtask.getId();
        if (subtasksIds.contains(id)) {
            startTimes.remove(subtask.getStartTime());
            if (!startTimes.isEmpty()) {
                startTime = startTimes.first();
            } else {
                startTime = null;
            }

            endTimes.remove(subtask.getEndTime());
            if (!endTimes.isEmpty()) {
                endTime = endTimes.last();
            } else {
                endTime = null;
            }

            if (startTime != null && endTime != null) {
                duration = Duration.between(startTime, endTime);
            } else {
                duration = Duration.ofMinutes(0);
            }

            subtasksIds.remove(id);

        } else {
            System.out.println("В эпике нет такой подзадачи");
        }
    }

    public void deleteAllSubtasks() {
        subtasksIds.clear();
        status = Status.NEW;
        startTime = null;
        endTime = null;
        duration = Duration.ofMinutes(0);
        startTimes.clear();
        endTimes.clear();
    }

    public HashSet<Integer> getSubtasksIds() {
        return subtasksIds;
    }


    @Override
    public String toString() {
        return "Epic{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                ", subtasksIds=" + subtasksIds.toString() +
                '}' +
                "\n";
    }

    //представлеие в виде строки вида ID,TYPE,NAME,STATUS,DESCRIPTION
    @Override
    public String toStringInFile() {
        String str = id + ",Epic," + name + "," + status + "," + description;
        return str;
    }

}
