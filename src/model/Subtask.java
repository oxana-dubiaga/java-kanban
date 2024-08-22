package model;

public class Subtask extends Task {

    private int epicId;

    public Subtask(String name, String discription, int id, int epicId) {
        super(name, discription, id);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        if (epicId == id) {
            System.out.println("Id Эпика совпадает с Id текущей подзадачи!");
        } else {
            this.epicId = epicId;
        }
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "epicId=" + epicId +
                ", name='" + name + '\'' +
                ", discription='" + discription + '\'' +
                ", id=" + id +
                ", status=" + status +
                '}';
    }
}
