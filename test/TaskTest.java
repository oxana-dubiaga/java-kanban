import model.Task;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TaskTest {

    @Test
    public void equalsTasksByIdCheck() {
        Task task1 = new Task("name", "description", 2);
        Task task2 = new Task("name", "description", 2);

        assertEquals(task1, task2, "Задачи с одинаковым id не равны");
    }








}
