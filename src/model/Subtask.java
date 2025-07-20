package model;

public class Subtask extends Task {

    private int epicId;

    public Subtask(String name, String discription, int id, int epicId) {
        super(name, discription, id);
        this.epicId = epicId;
    }

    //Конструктор с учетом времени на задачу
    public Subtask(String name, String discription, int id, int epicId, int durationMinutes, String startTimeString) {
        super(name, discription, id, durationMinutes, startTimeString);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "epicId=" + epicId +
                ", name='" + name + '\'' +
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

    //представлеие в виде строки вида ID,TYPE,NAME,STATUS,DESCRIPTION,EPIC, StartTime, duration
    @Override
    public String toStringInFile() {
        String str = id + ",Subtask," + name + "," + status + "," + description + "," + epicId + "," + startTime.format(formatter) + "," + (duration.getSeconds() / 60);
        return str;
    }
}
