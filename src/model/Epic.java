package model;

import java.util.HashSet;

public class Epic extends Task {

    private HashSet<Integer> subtasksIds;

    public Epic(String name, String discription, int id) {
        super(name, discription, id);
        subtasksIds = new HashSet<>();
    }

    public void addSubtask(Subtask subtask) {
        int id = subtask.getId();
        if (subtasksIds.contains(id)) {
            System.out.println("Такая подзадача уже есть в данном эпике");
        } else {
            subtasksIds.add(id);
        }
    }

    public void deleteSubtask(Subtask subtask) {
        int id = subtask.getId();
        if (subtasksIds.contains(id)) {
            subtasksIds.remove(id);
        } else {
            System.out.println("В эпике нет такой подзадачи");
        }
    }

    public void deleteAllSubtasks() {
        subtasksIds.clear();
        status = Status.NEW;
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
                '}';
    }
}
