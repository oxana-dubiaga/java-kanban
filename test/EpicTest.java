import model.Epic;
import model.Subtask;
import org.junit.jupiter.api.Test;
import service.NoStartOrEndTimeException;

import java.time.Duration;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
        Subtask subtask1 = new Subtask("name", "description", subtaskId1, epicId, 30, "12.02.1990 11:00");
        int subtaskId2 = 5;
        Subtask subtask2 = new Subtask("name", "description", subtaskId2, epicId, 10, "15.06.2000 14:00");

        Duration zero = Duration.ofMinutes(0);
        assertEquals(zero, epic1.getDuration(), "У пустого эпика продолжительность отлична от нуля");
        assertThrows(NoStartOrEndTimeException.class, () -> epic1.getStartTime(),
                "При вызове getStartTime() у созданного пустого эпика не кидается исключение");
        assertThrows(NoStartOrEndTimeException.class, () -> epic1.getEndTime(),
                "При вызове getStartTime() у созданного пустого эпика не кидается исключение");

        epic1.addSubtask(subtask1);
        HashSet<Integer> epicsSubrasks = epic1.getSubtasksIds();
        assertEquals(1, epicsSubrasks.size(), "Подзадача не добавляется");

        epic1.deleteSubtask(subtask1);
        epicsSubrasks = epic1.getSubtasksIds();
        assertEquals(0, epicsSubrasks.size(), "Подзадача не удаляется");

        epic1.addSubtask(subtask1);
        epic1.addSubtask(subtask2);
        Duration expectedDuration = Duration.between(subtask1.getStartTime(), subtask2.getEndTime());
        assertEquals(expectedDuration, epic1.getDuration(), "Неправильный расчет продолжительности эпика");
        assertEquals(subtask1.getStartTime(), epic1.getStartTime(), "Неправильное время начала эпика");
        assertEquals(subtask2.getEndTime(), epic1.getEndTime(), "Неправильное время окончания эпика");

        epic1.deleteAllSubtasks();
        epicsSubrasks = epic1.getSubtasksIds();
        assertEquals(0, epicsSubrasks.size(), "Все подзадачи не удаляется");
        assertThrows(NoStartOrEndTimeException.class, () -> epic1.getStartTime(),
                "При вызове getStartTime() у эпика c удаленными подзадачами не кидается исключение");
        assertThrows(NoStartOrEndTimeException.class, () -> epic1.getEndTime(),
                "При вызове getStartTime() у эпика c удаленными подзадачами не кидается исключение");
        assertEquals(zero, epic1.getDuration(), "У эпика c удаленными подзадачами продолжительность отлична от нуля");
    }


}
