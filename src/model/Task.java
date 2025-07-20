package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Task {

    //добавила модификатор доступа. указан protected, а не private, чтобы поля были доступны наследникам,
    //в которых мы обращаемся к этим полям (например в toString)
    protected String name;
    protected String description;
    protected int id;
    protected Status status;
    protected Duration duration;
    protected LocalDateTime startTime;

    protected DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");


    public Task(String name, String description, int id) {
        this.name = name;
        this.description = description;
        this.id = id;
        status = Status.NEW;
    }

    //Конструктор с учетом времени на задачу
    public Task(String name, String description, int id, int durationMinutes, String startTimeString) {
        this.name = name;
        this.description = description;
        this.id = id;
        status = Status.NEW;
        duration = Duration.ofMinutes(durationMinutes);
        startTime = LocalDateTime.parse(startTimeString, formatter);
    }


    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getId() {
        return id;
    }

    public Status getStatus() {
        return status;
    }

    public Duration getDuration() {
        return duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        LocalDateTime endTime = startTime.plus(duration);
        return endTime;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                ", startTime=" + startTime.format(formatter) +
                ", duration=" + duration +
                ", startTime=" + startTime.format(formatter) +
                ", endTime=" + getEndTime().format(formatter) +
                '}' +
                "\n";
    }

    //представлеие в виде строки вида ID,TYPE,NAME,STATUS,DESCRIPTION, StartTime, duration
    public String toStringInFile() {
        String str = id + ",Task," + name + "," + status + "," + description + "," + startTime.format(formatter) + "," + (duration.getSeconds() / 60);
        return str;
    }

}
