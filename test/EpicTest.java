import model.Epic;
import model.Subtask;
import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EpicTest {

    @Test
    public void equalsEpicsByIdCheck() {
        Epic epic1 = new Epic("name", "description", 3);
        Epic epic2 = new Epic("name", "description", 3);

        assertEquals(epic1, epic2, "Эпики с одинаковым id не равны");
    }

    @Test
    public void addAndDeleteSubtask() {
        int epicId = 3;
        Epic epic1 = new Epic("name", "description", 3);
        int subtaskId1 = 4;
        Subtask subtask1 = new Subtask("name", "description", subtaskId1, epicId);
        int subtaskId2 = 5;
        Subtask subtask2 = new Subtask("name", "description", subtaskId2, epicId);

        epic1.addSubtask(subtask1);
        HashSet<Integer> epicsSubrasks = epic1.getSubtasksIds();
        assertEquals(1, epicsSubrasks.size(), "Подзадача не добавляется");

        epic1.deleteSubtask(subtask1);
        epicsSubrasks = epic1.getSubtasksIds();
        assertEquals(0, epicsSubrasks.size(), "Подзадача не удаляется");

        epic1.addSubtask(subtask1);
        epic1.addSubtask(subtask2);
        epic1.deleteAllSubtasks();
        epicsSubrasks = epic1.getSubtasksIds();
        assertEquals(0, epicsSubrasks.size(), "Все подзадачи не удаляется");
    }


}
