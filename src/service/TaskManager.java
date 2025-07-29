package service;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.List;
import java.util.TreeSet;

public interface TaskManager {

    //гетеры для трех видов задач по id
    Task getTask(int id);

    Epic getEpic(int id);

    Subtask getSubtask(int id);


    //получение списка всех задач для трех видов
    List<Task> getAllTasks();

    List<Epic> getAllEpics();

    List<Subtask> getAllSubtasks();


    //добавление трех видов задач
    void addNewTask(Task newTask);

    void addNewEpic(Epic newEpic);

    void addNewSubtask(Subtask newSubtask);


    //очистка списков для трех видов задач
    void deleteALLTasks();

    void deleteAllEpic();

    void deleteAllSubtasks();


    //удаление по id для трех видов задач
    void deleteTask(int id);

    void deleteEpic(int id);

    void deleteSubtask(int id);


    //обновление для трех видов задач
    void updateTask(Task newTask);

    void updateEpic(Epic newEpic);

    void updateSubtask(Subtask newSubtask);


    List<Subtask> getEpicsSubtasks(Epic epic);

    //получение сортированного по времени списка задач
    TreeSet<Task> getPrioritizedTasks();

    //проверка пересечения по времени двух задач
    Boolean isIntersects(Task task1, Task task2);

    //получение истории просмотров
    List<Task> getHistory();

    //очистка истории
    void cleanHistory();


}
