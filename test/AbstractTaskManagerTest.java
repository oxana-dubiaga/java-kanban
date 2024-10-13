import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.TaskManager;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

abstract class AbstractTaskManagerTest<T extends TaskManager> {
    public T taskManager;

    @BeforeEach
    public void setTaskManager() {
        taskManager = createTaskManager();
    }

    protected abstract T createTaskManager();


    @Test
    public void operationWithTaskInManager() {
        int taskId = 1;
        Task task = new Task("Task", "description", taskId);
        taskManager.addNewTask(task);

        Task savedTask = taskManager.getTask(taskId);

        //проверка добавления
        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        //проверка получения списка задач
        List<Task> tasks = taskManager.getAllTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");

        //проверка что статус по дефолту new
        Status expectedStatus = Status.NEW;
        Status resultStatus = taskManager.getTask(taskId).getStatus();
        assertEquals(expectedStatus, resultStatus, "Неверный дефолтный статус задачи");

        //проверка смены статуса
        Status setStatus = Status.IN_PROGRESS;
        taskManager.getTask(taskId).setStatus(setStatus);
        Status resultStatusAfterChanging = taskManager.getTask(taskId).getStatus();
        assertEquals(setStatus, resultStatusAfterChanging, "Не изменяется статус задачи");

        //проверка обновления задачи
        String newName = "new name";
        Task updatedTask = new Task(newName, "---", taskId);
        taskManager.updateTask(updatedTask);
        String resultName = taskManager.getTask(taskId).getName();
        assertEquals(newName, resultName, "Задача не обновляется");

        //проверка удаления задачи
        taskManager.deleteTask(taskId);
        tasks = taskManager.getAllTasks();
        assertEquals(0, tasks.size(), "Задача не удаляется");
    }


    @Test
    public void operationWithEpicInManager() {
        int epicId = 1;
        Epic epic = new Epic("Epic", "description", epicId);
        taskManager.addNewEpic(epic);

        Epic savedEpic = taskManager.getEpic(epicId);

        //проверка добавления эпика
        assertNotNull(savedEpic, "Эпик не найден.");
        assertEquals(epic, savedEpic, "Эпики не совпадают.");

        //проверка получения списка эпиков
        List<Epic> epics = taskManager.getAllEpics();

        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество эпиков.");
        assertEquals(epic, epics.get(0), "Эпики не совпадают.");

        //проверка что статус эпика по дефолту new
        Status expectedEpicStatus = Status.NEW;
        Status resultEpicStatus = taskManager.getEpic(epicId).getStatus();
        assertEquals(expectedEpicStatus, resultEpicStatus, "Неверный дефолтный статус эпика");

        //проверка смены статуса эпика вручную
        Status setEpicStatus = Status.DONE;
        taskManager.getEpic(epicId).setStatus(setEpicStatus);
        Status resultEpicStatusAfterChanging = taskManager.getEpic(epicId).getStatus();
        assertEquals(setEpicStatus, resultEpicStatusAfterChanging, "Не изменяется статус эпика в ручную");

        //проверка обновления эпика
        String newName = "new name";
        Epic updatedEpic = new Epic(newName, "---", epicId);
        taskManager.updateEpic(updatedEpic);
        String resultName = taskManager.getEpic(epicId).getName();
        assertEquals(newName, resultName, "Эпик не обновляется");

        //проверка удаления эпика
        taskManager.deleteEpic(epicId);
        epics = taskManager.getAllEpics();
        assertEquals(0, epics.size(), "Эпик не удаляется");
    }

    @Test
    public void operationWithSubtaskInManager() {
        //добавление подзадачи
        //для существования подзадачи в менеджере нужен также и её эпик
        int epicId = 1;
        Epic epic = new Epic("Epic", "description", epicId);
        taskManager.addNewEpic(epic);
        int subtaskId = 2;
        Subtask subtask = new Subtask("name", "---", subtaskId, 1);
        taskManager.addNewSubtask(subtask);

        //проверка получения списка подзадач
        List<Subtask> subtasks = taskManager.getAllSubtasks();
        assertNotNull(subtasks, "Подзадачи не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество подзадач.");
        assertEquals(subtask, subtasks.get(0), "Подзадачи не совпадают.");

        //получение id эпика
        int resultEpicId = subtask.getEpicId();
        assertEquals(epicId, resultEpicId, "Возвращается неверный id эпика");

        //проверка что статус подзадачи по дефолту new
        Status expectedSubtaskStatus = Status.NEW;
        Status resultSubtaskStatus = taskManager.getSubtask(subtaskId).getStatus();
        assertEquals(expectedSubtaskStatus, resultSubtaskStatus, "Неверный дефолтный статус подзадачи");

        //проверка смены статуса подзадачи
        Status setSubtaskStatus = Status.IN_PROGRESS;
        taskManager.getSubtask(subtaskId).setStatus(setSubtaskStatus);
        Status resultSubtaskStatusAfterChanging = taskManager.getSubtask(subtaskId).getStatus();
        assertEquals(setSubtaskStatus, resultSubtaskStatusAfterChanging, "Не изменяется статус подзадачи");

        //проверка обновления подзадачи
        String newName = "new name";
        Subtask updateSubtask = new Subtask(newName, "---", subtaskId, epicId);
        taskManager.updateSubtask(updateSubtask);
        String resultName = taskManager.getSubtask(subtaskId).getName();
        assertEquals(newName, resultName, "Подзадача не обновляется");

        //проверка удаления подзадачи
        taskManager.deleteSubtask(subtaskId);
        subtasks = taskManager.getAllSubtasks();
        assertEquals(0, subtasks.size(), "Подзадача не удаляется");
    }


    @Test
    public void interactionOfEpicsAndSubtasksInManager() {
        //добавление эпика
        int epicId = 1;
        Epic epic = new Epic("Epic", "description", epicId);
        taskManager.addNewEpic(epic);

        //добавление подзадачи
        int subtaskId = 2;
        Subtask subtask = new Subtask("name", "---", subtaskId, epicId);
        taskManager.addNewSubtask(subtask);

        //получение списка подзадач у эпика
        List<Subtask> epicsSubtasks = taskManager.getEpicsSubtasks(epic);
        assertNotNull(epicsSubtasks, "Подзадачи эпика не возвращаются.");
        assertEquals(1, epicsSubtasks.size(), "Неверное количество подзадач у эпика.");
        assertEquals(subtask, epicsSubtasks.get(0), "Подзадачи эпика не совпадают.");


        //после обновления подзадачи со статусом in progress статус эпика должен стать in progress
        Status setSubtaskStatus = Status.IN_PROGRESS;
        taskManager.getSubtask(subtaskId).setStatus(setSubtaskStatus);
        taskManager.updateSubtask(taskManager.getSubtask(subtaskId));
        Status expectedEpicStatusAfterAddingSubtask = Status.IN_PROGRESS;

        Status resultEpicStatusAfterAddingSubtask = taskManager.getEpic(epicId).getStatus();

        assertEquals(expectedEpicStatusAfterAddingSubtask, resultEpicStatusAfterAddingSubtask,
                "Не обновляется статус эпика после добавления подзадачи");


        //после завершения подзадачи статус эпика должен стать done
        Status expectedEpicStatusAfterFinishingSubtask = Status.DONE;
        taskManager.getSubtask(subtaskId).setStatus(Status.DONE);
        taskManager.updateSubtask(taskManager.getSubtask(subtaskId));

        Status resultEpicStatusAfterFinishingSubtask = taskManager.getEpic(epicId).getStatus();

        assertEquals(expectedEpicStatusAfterFinishingSubtask, resultEpicStatusAfterFinishingSubtask,
                "Не обновляется статус эпика после завершения подзадачи");

        //удаление подзадачи - проверка статуса эпика и списка подзадач эпика
        taskManager.deleteSubtask(subtaskId);
        Status expectedEpicStatusAfterDeletingSubtask = Status.NEW;
        Status resultEpicStatusAfterDeletingSubtask = taskManager.getEpic(epicId).getStatus();
        epicsSubtasks = taskManager.getEpicsSubtasks(epic);

        assertEquals(expectedEpicStatusAfterDeletingSubtask, resultEpicStatusAfterDeletingSubtask,
                "Не обновляется статус эпика после удаления подзадачи");
        assertEquals(0, epicsSubtasks.size(),
                "При удалении подзадачи она не удаляется из списка подзадач эпика");

        //При удалении эпика должны удалится его подзадачи
        taskManager.addNewSubtask(subtask);
        taskManager.deleteEpic(epicId);
        List<Subtask> allSubtasks = taskManager.getAllSubtasks();
        assertEquals(0, allSubtasks.size(), "При удалении эпика не удаляются подзадачи");
    }

    @Test
    public void historyInManager() {
        int taskId = 1;
        Task task = new Task("Task", "description", taskId);
        int epicId = 2;
        Epic epic = new Epic("Epic", "description", epicId);
        int subtask1Id = 3;
        Subtask subtask1 = new Subtask("name", "---", subtask1Id, epicId);
        int subtask2Id = 4;
        Subtask subtask2 = new Subtask("name", "---", subtask2Id, epicId);
        taskManager.addNewTask(task);
        taskManager.addNewEpic(epic);
        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask2);

        //проверка что список просмотров создается пустым
        List<Task> history = new ArrayList<>();
        history = taskManager.getHistory();
        assertEquals(0, history.size(), "Список просмотров не пуст при создании");

        //проверка просмотра задач
        taskManager.getTask(taskId);
        history = taskManager.getHistory();
        assertEquals(1, history.size(), "Просмотры задач не добавляются в историю");

        //проверка очистки списка просмотров
        taskManager.cleanHistory();
        history = taskManager.getHistory();
        assertEquals(0, history.size(), "Список просмотров не очищается");

        //проверка просмотров эпиков
        taskManager.cleanHistory();
        taskManager.getEpic(epicId);
        history = taskManager.getHistory();
        assertEquals(1, history.size(), "Просмотры эпиков не добавляются в историю");
        taskManager.cleanHistory();

        //проверка просмотров подзадач
        taskManager.getSubtask(subtask1Id);
        history = taskManager.getHistory();
        assertEquals(1, history.size(), "Просмотры подзадач не добавляются в историю");
        taskManager.cleanHistory();

        //проверка что при удалении задачи она пропадает из истории
        taskManager.getTask(taskId);
        taskManager.deleteTask(taskId);
        history = taskManager.getHistory();
        assertEquals(0, history.size(), "Удаляемые задачи остаются в истории");


        //проверка что при удалении подзадачи она пропадает из истории
        taskManager.getSubtask(subtask1Id);
        taskManager.deleteSubtask(subtask1Id);
        history = taskManager.getHistory();
        assertEquals(0, history.size(), "Удаляеые подзадачи остаются в истории");


        //проверка что при удалении эпика пропадает из истории он сам и его подзадачи
        taskManager.getEpic(epicId);
        taskManager.deleteSubtask(subtask2Id);
        taskManager.deleteEpic(epicId);
        history = taskManager.getHistory();
        assertEquals(0, history.size(), "Удаляеые подзадачи остаются в истории");


    }
}
