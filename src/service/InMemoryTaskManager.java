package service;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {

    private HashMap<Integer, Task> tasks;
    private HashMap<Integer, Epic> epics;
    private HashMap<Integer, Subtask> subtasks;
    int currentId = 1;

    private HistoryManager historyManager;


    public InMemoryTaskManager(HistoryManager historyManager) {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
        this.historyManager = historyManager;
    }


    //гетеры для трех видов задач по id
    @Override
    public Task getTask(int id) {
        if (tasks.containsKey(id)) {
            Task task = tasks.get(id);
            historyManager.add(task);
            return task;
        } else {
            //возможно, для дальнейшей работы в подобных местах лучше выбрасывать исключение, а не врзвращать null, в ТЗ не было указано
            System.out.println("Задачи не существует");
            return null;
        }
    }

    @Override
    public Epic getEpic(int id) {
        if (epics.containsKey(id)) {
            Epic epic = epics.get(id);
            historyManager.add(epic);
            return epic;
        } else {
            System.out.println("Эпика не существует");
            return null;
        }
    }

    @Override
    public Subtask getSubtask(int id) {
        if (subtasks.containsKey(id)) {
            Subtask subtask = subtasks.get(id);
            historyManager.add(subtask);
            return subtask;
        } else {
            System.out.println("Подзадачи не существует");
            return null;
        }
    }


    //получение списка всех задач для трех видов
    @Override
    public List<Task> getAllTasks() {
        ArrayList<Task> allTasks = new ArrayList<>(tasks.values());
        return allTasks;
    }

    @Override
    public List<Epic> getAllEpics() {
        ArrayList<Epic> allEpics = new ArrayList<>(epics.values());
        return allEpics;
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        ArrayList<Subtask> allSubtasks = new ArrayList<>(subtasks.values());
        return allSubtasks;
    }


    //добавление трех видов задач
    @Override
    public void addNewTask(Task newTask) {
        if (newTask != null) {
            newTask.setId(currentId);
            currentId++;
            tasks.put(newTask.getId(), newTask);
        }
    }

    @Override
    public void addNewEpic(Epic newEpic) {
        if (newEpic != null) {
            newEpic.setId(currentId);
            currentId++;
            newEpic = updateEpicStatus(newEpic);
            epics.put(newEpic.getId(), newEpic);
        }
    }

    @Override
    public void addNewSubtask(Subtask newSubtask) {
        if (newSubtask != null) {
            int parentEpicId = newSubtask.getEpicId();
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
    }


    //очистка списков для трех видов задач
    @Override
    public void deleteALLTasks() {
        tasks.clear();
    }

    @Override
    public void deleteAllEpic() {
        epics.clear();
        //удаление всех эпиков подразумевает, что удаляются и все связанные с ними подзадачи
        subtasks.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        subtasks.clear();
        //удаление всех подзадач у эпиков
        for (int i : epics.keySet()) {
            Epic epic = epics.get(i);
            epic.deleteAllSubtasks();
            //в методе deleteAllSubtasks() класса Epic при удалении всех подзадач эпику сразу присваивается статус NEW,
            //поэтому здесь дополнительно не нужно делать обновление статуса эпика
            epics.put(i, epic);
        }
    }


    //удаление по id для трех видов задач
    @Override
    public void deleteTask(int id) {
        tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void deleteEpic(int id) {
        for (int subtaskId : epics.get(id).getSubtasksIds()) {
            subtasks.remove(subtaskId);
            historyManager.remove(subtaskId);
        }
        epics.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void deleteSubtask(int id) {
        Subtask s = subtasks.get(id);
        Epic parentEpic = epics.get(s.getEpicId());
        parentEpic.deleteSubtask(s);
        parentEpic = updateEpicStatus(parentEpic);
        epics.put(parentEpic.getId(), parentEpic);
        subtasks.remove(id);
        historyManager.remove(id);
    }


    //обновление для трех видов задач
    @Override
    public void updateTask(Task newTask) {
        if (newTask != null) {
            int id = newTask.getId();
            if (!tasks.containsKey(id)) {
                System.out.println("Такой задачи нет в списке");
            } else {
                tasks.put(id, newTask);
            }
        }
    }

    //в методе при обновлении удаляются из списка подзадач те, которые были в старой версии эпика, но отсутствуют в новой
    @Override
    public void updateEpic(Epic newEpic) {
        if (newEpic != null) {
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
    }

    //в методе при обновлении подзадачи проверяется, изменился ли эпик, которому принадлежит подзадача,
    //и обновляются статусы либо текущего эпика, либо старого и нового эпика (в случае измения принадлежности)
    @Override
    public void updateSubtask(Subtask newSubtask) {
        if (newSubtask != null) {
            int id = newSubtask.getId();
            if (!subtasks.containsKey(id)) {
                System.out.println("Такой подзадачи нет в списке");
            } else {
                Subtask oldSubtask = subtasks.get(id);
                int oldParentEpicId = oldSubtask.getEpicId();
                int newParentEpicId = newSubtask.getEpicId();
                Epic parentEpic = epics.get(newParentEpicId);
                if (oldParentEpicId != newParentEpicId) {
                    Epic oldParentEpic = epics.get(oldParentEpicId);
                    oldParentEpic.deleteSubtask(newSubtask);
                    oldParentEpic = updateEpicStatus(oldParentEpic);
                    epics.put(oldParentEpicId, oldParentEpic);
                    parentEpic.addSubtask(newSubtask);
                }
                subtasks.put(id, newSubtask);
                parentEpic = updateEpicStatus(parentEpic);
                epics.put(newParentEpicId, parentEpic);
            }
        }
    }


    @Override
    public List<Subtask> getEpicsSubtasks(Epic epic) {
        if (epic != null) {
            ArrayList<Subtask> subtasksInEpic = new ArrayList<>();
            HashSet<Integer> subtasksIds = epic.getSubtasksIds();
            for (int id : subtasksIds) {
                subtasksInEpic.add(subtasks.get(id));
            }
            return subtasksInEpic;
        }
        return null;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getTasks();
    }

    @Override
    public void cleanHistory() {
        historyManager.clearHistory();
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
