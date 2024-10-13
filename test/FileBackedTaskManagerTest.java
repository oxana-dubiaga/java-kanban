import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.Test;
import service.ManagerSaveException;
import service.Managers;
import service.TaskManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class FileBackedTaskManagerTest extends AbstractTaskManagerTest {

    File testFile;

    {
        try {
            testFile = File.createTempFile("data", "csv");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected TaskManager createTaskManager() {
        return Managers.getFileBackedTaskManager(testFile.getPath());
    }

    @Test
    public void addAndDeleteTasks() {
        int taskId = 1;
        Task task = new Task("Task", "description", taskId);

        //проверка добавления задачи
        taskManager.addNewTask(task);
        assertEquals(getListFromFile().size(), 1, "Задача не добавляется в файл.");
        //проверка удаления задачи
        taskManager.deleteTask(taskId);
        assertEquals(getListFromFile().size(), 0, "Задача не удаляется из файла.");
    }

    @Test
    public void addAndDeleteEpicAndSubtask() {
        int epicId = 1;
        Epic epic = new Epic("Epic", "description", epicId);
        int subtaskId = 2;
        Subtask subtask = new Subtask("name", "---", subtaskId, 1);


        //проверка добавления эпика
        taskManager.addNewEpic(epic);
        assertEquals(getListFromFile().size(), 1, "Эпик не добавляется в файл.");
        //проверка добавления сабтаски
        taskManager.addNewSubtask(subtask);
        assertEquals(getListFromFile().size(), 2, "Подзадача не добавляется в файл.");

        //проверка удаления сабтаски
        taskManager.deleteSubtask(subtaskId);
        assertEquals(getListFromFile().size(), 1, "Подзадача не удаляется из файла.");
        //проверка удаления эпика
        taskManager.deleteEpic(epicId);
        assertEquals(getListFromFile().size(), 0, "Эпик не удаляется из файла.");
    }


    private List<String> getListFromFile() {
        List<String> list;
        try {
            list = Files.readAllLines(testFile.toPath());
        } catch (IOException ex) {
            throw new ManagerSaveException("Ошибка при чтении из файла");
        }
        return list;
    }

}


