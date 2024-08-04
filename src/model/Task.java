package model;

public class Task {

    //добавила модификатор доступа. указан protected, а не private, чтобы поля были доступны наследникам,
    //в которых мы обращаемся к этим полям (например в toString)
    protected String name;
    protected String discription;
    protected int id;
    protected Status status;

    public Task(String name, String discription, int id) {
        this.name = name;
        this.discription = discription;
        this.id = id;
        status = Status.NEW;
    }

    public String getName() {
        return name;
    }

    public String getDiscription() {
        return discription;
    }

    public int getId() {
        return id;
    }

    public Status getStatus() {
        return status;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDiscription(String discription) {
        this.discription = discription;
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
                ", discription='" + discription + '\'' +
                ", id=" + id +
                ", status=" + status +
                '}';
    }
}
