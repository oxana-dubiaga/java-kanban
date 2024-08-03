package service;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class TaskManager {

    HashMap<Integer, Task> tasks;
    HashMap<Integer, Epic> epics;
    HashMap<Integer, Subtask> subtasks;
    static int currentId = 1;

    public TaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
    }


    //гетеры для трех видов задач по id
    public Task getTask(int id) {
        if (tasks.containsKey(id)) {
            return tasks.get(id);
        } else {
            //возможно, для дальнейшей работы в подобных местах лучше выбрасывать исключение, а не врзвращать null, в ТЗ не было указано
            System.out.println("Задачи не существует");
            return null;
        }
    }

    public Epic getEpic(int id) {
        if (epics.containsKey(id)) {
            return epics.get(id);
        } else {
            System.out.println("Эпика не существует");
            return null;
        }
    }

    public Subtask getSubtask(int id) {
        if (subtasks.containsKey(id)) {
            return subtasks.get(id);
        } else {
            System.out.println("Подзадачи не существует");
            return null;
        }
    }


    //получение списка всех задач для трех видов
    public HashMap<Integer, Task> getAllTasks() {
        return tasks;
    }

    public HashMap<Integer, Epic> getAllEpics() {
        return epics;
    }

    public HashMap<Integer, Subtask> getAllSubtasks() {
        return subtasks;
    }


    //добавление трех видов задач
    public void addNewTask(Task newTask) {
        newTask.setId(currentId);
        currentId++;
        tasks.put(newTask.getId(), newTask);
    }

    public void addNewEpic(Epic newEpic) {
        newEpic.setId(currentId);
        currentId++;
        newEpic = updateEpicStatus(newEpic);
        epics.put(newEpic.getId(), newEpic);
    }

    public void addNewSubtask(Subtask newSubtask) {
        int parentEpicId = newSubtask.getParenEpicId();
        if (!epics.containsKey(parentEpicId)) {
            System.out.println("Эпика, указанного в подзадаче, не существует, подзадача не добавлена");
        } else {
            newSubtask.setId(currentId);
            currentId++;
            subtasks.put(newSubtask.getId(), newSubtask);
            Epic parentEpic = epics.get(parentEpicId);
            parentEpic.addSubtask(newSubtask);
            parentEpic = updateEpicStatus(parentEpic);
            epics.put(parentEpicId, parentEpic);
        }
    }


    //очистка списков для трех видов задач
    public void deleteALLTasks() {
        tasks.clear();
    }

    public void deleteAllEpic() {
        epics.clear();
        //удаление всех эпиков подразумевает, что удаляются и все связанные с ними подзадачи
        subtasks.clear();
    }

    public void deleteAllSubtasks() {
        subtasks.clear();
        //удаление всех подзадач у эпиков
        for (int i : epics.keySet()) {
            Epic epic = epics.get(i);
            epic.deleteAllSubtasks();
            epics.put(i, epic);
        }
    }


    //удаление по id для трех видов задач
    public void deleteTask(int id) {
        tasks.remove(id);
    }

    public void deleteEpic(int id) {
        for (int subtaskId : epics.get(id).getSubtasksIds()) {
            subtasks.remove(subtaskId);
        }
        epics.remove(id);
    }

    public void deleteSubtask(int id) {
        Subtask s = subtasks.get(id);
        Epic parentEpic = epics.get(s.getParenEpicId());
        parentEpic.deleteSubtask(s);
        parentEpic = updateEpicStatus(parentEpic);
        epics.put(parentEpic.getId(), parentEpic);
        subtasks.remove(id);
    }


    //обновление для трех видов задач
    public void updateTask(Task newTask) {
        int id = newTask.getId();
        if (!tasks.containsKey(id)) {
            System.out.println("Такой задачи нет в списке");
        } else {
            tasks.put(id, newTask);
        }
    }

    //в методе при обновлении удаляются из списка подзадач те, которые были в старой версии эпика, но отсутствуют в новой
    public void updateEpic(Epic newEpic) {
        int id = newEpic.getId();
        if (!epics.containsKey(id)) {
            System.out.println("Такого эпика нет в списке");
        } else {
            Epic oldEpic = epics.get(id);
            HashSet<Integer> oldSubtasks = oldEpic.getSubtasksIds();
            HashSet<Integer> newSubtasks = newEpic.getSubtasksIds();
            for (int oldId : oldSubtasks) {
                if (!newSubtasks.contains(oldId)) {
                    subtasks.remove(oldId);
                }
            }
            epics.put(id, newEpic);
        }
    }

    //в методе при обновлении подзадачи проверяется, изменился ли эпик, которому принадлежит подзадача,
    //и обновляются статусы либо текущего эпика, либо старого и нового эпика (в случае измения принадлежности)
    public void updateSubtask(Subtask newSubtask) {
        int id = newSubtask.getId();
        if (!subtasks.containsKey(id)) {
            System.out.println("Такой подзадачи нет в списке");
        } else {
            Subtask oldSubtask = subtasks.get(id);
            int oldParentEpicId = oldSubtask.getParenEpicId();
            int newParentEpicId = newSubtask.getParenEpicId();
            if (oldParentEpicId != newParentEpicId) {
                Epic oldParentEpic = epics.get(oldParentEpicId);
                oldParentEpic.deleteSubtask(newSubtask);
                oldParentEpic = updateEpicStatus(oldParentEpic);
                epics.put(oldParentEpicId, oldParentEpic);
            }
            subtasks.put(id, newSubtask);
            Epic parentEpic = epics.get(newParentEpicId);
            parentEpic = updateEpicStatus(parentEpic);
            epics.put(newParentEpicId, parentEpic);
        }
    }


    public ArrayList<Subtask> getEpicsSubtasks(Epic epic) {
        ArrayList<Subtask> subtasksInEpic = new ArrayList<>();
        HashSet<Integer> subtasksIds = epic.getSubtasksIds();
        for (int id : subtasksIds) {
            subtasksInEpic.add(subtasks.get(id));
        }
        return subtasksInEpic;
    }


    //вспомогательный метод: обновление статуса эпика в зависимости от статусов его подзадач
    private Epic updateEpicStatus(Epic epic) {
        HashSet<Integer> subtasksIds = epic.getSubtasksIds();
        ArrayList<Status> subtasksStatuses = new ArrayList<>();
        for (int id : subtasksIds) {
            subtasksStatuses.add(subtasks.get(id).getStatus());
        }

        boolean isAllNew = true;
        boolean isAllDone = true;
        if (subtasksStatuses.isEmpty()) {
            isAllDone = false;
        }
        for (Status st : subtasksStatuses) {
            if (st != Status.NEW) {
                isAllNew = false;
            }
            if (st != Status.DONE) {
                isAllDone = false;
            }
        }

        if (isAllNew) {
            epic.setStatus(Status.NEW);
        } else if (isAllDone) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }

        return epic;
    }


}
