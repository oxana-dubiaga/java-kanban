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

    //Убрала проверку, добавляла только потому, что тз был такой рекомендуемый тест
    // "проверьте, что объект Epic нельзя добавить в самого себя в виде подзадачи";
    //но это и так нельзя было бы сделать, так как метод добавления у эпика принимает
    // на вход сабтаск и передать в него сам эпик не получилось бы
    //я решила на всякий случай еще ограничить возможность, чтобы у эпика и сабтаска был одинаковый Id
    //но это наверное действительно излишнее
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
                '}';
    }

    //представлеие в виде строки вида ID,TYPE,NAME,STATUS,DESCRIPTION,EPIC
    @Override
    public String toStringInFile() {
        String str = id + ",Subtask," + name + "," + status + "," + description + "," + epicId;
        return str;
    }
}
