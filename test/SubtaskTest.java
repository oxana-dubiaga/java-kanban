import model.Subtask;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SubtaskTest {

    @Test
    public void equalsSubtasksByIdCheck() {
        Subtask subtask1 = new Subtask("name", "description", 4, 3);
        Subtask subtask2 = new Subtask("name", "description", 4, 3);

        assertEquals(subtask1, subtask2, "Подзадачи с одинаковым id не равны");
    }

    public void changeEpicId() {
        int epicId = 2;
        Subtask subtask = new Subtask("name", "description", 1, 2);
        int newEpicId = 3;

        subtask.setEpicId(newEpicId);
        int resultEpicId = subtask.getEpicId();

        assertEquals(newEpicId, resultEpicId, "Не изменяется Id эпика");
    }

}
