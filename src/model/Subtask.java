package model;

public class Subtask extends Task {

    int parenEpicId;

    public Subtask(String name, String discription, int id, int parenEpicId) {
        super(name, discription, id);
        this.parenEpicId = parenEpicId;
    }

    public int getParenEpicId() {
        return parenEpicId;
    }

    public void setParenEpicId(int parenEpicId) {
        this.parenEpicId = parenEpicId;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "parenEpicId=" + parenEpicId +
                ", name='" + name + '\'' +
                ", discription='" + discription + '\'' +
                ", id=" + id +
                ", status=" + status +
                '}';
    }
}
