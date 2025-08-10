package service;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    private HashMap<Integer, Task> tasks;
    private HashMap<Integer, Epic> epics;
    private HashMap<Integer, Subtask> subtasks;
    int currentId = 1;

    private TreeSet<Task> prioritizedTasks = new TreeSet<>(new TaskComparatorByStartTime());

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
    public int addNewTask(Task newTask) {
        if (newTask != null) {
            if (prioritizedTasks.stream().anyMatch(task -> isIntersects(task, newTask))) {
                System.out.println("Добавляемая задача " + newTask.getName() + " накладывается на уже существующую. Задача не добавлена");
                return 1;
            } else {
                newTask.setId(currentId);
                currentId++;
                tasks.put(newTask.getId(), newTask);
                prioritizedTasks.add(newTask);
            }
        }
        return 0;
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
    public int addNewSubtask(Subtask newSubtask) {
        if (newSubtask != null) {
            int parentEpicId = newSubtask.getEpicId();
            if (!epics.containsKey(parentEpicId)) {
                System.out.println("Эпика, указанного в подзадаче, не существует, подзадача не добавлена");
                return 2;
            } else {
                if (prioritizedTasks.stream().anyMatch(task -> isIntersects(task, newSubtask))) {
                    System.out.println("Добавляемая подзадача " + newSubtask.getName() + " накладывается на уже существующую. Задача не добавлена");
                    return 1;
                } else {
                    newSubtask.setId(currentId);
                    currentId++;
                    subtasks.put(newSubtask.getId(), newSubtask);
                    Epic parentEpic = epics.get(parentEpicId);
                    parentEpic.addSubtask(newSubtask);
                    parentEpic = updateEpicStatus(parentEpic);
                    epics.put(parentEpicId, parentEpic);
                    prioritizedTasks.add(newSubtask);
                }
            }
        }
        return 0;
    }


    //очистка списков для трех видов задач
    @Override
    public void deleteALLTasks() {
        tasks.keySet().forEach(id -> prioritizedTasks.remove(tasks.get(id)));
        tasks.clear();
    }

    @Override
    public void deleteAllEpic() {
        epics.clear();
        //удаление всех эпиков подразумевает, что удаляются и все связанные с ними подзадачи
        subtasks.keySet().forEach(id -> prioritizedTasks.remove(subtasks.get(id)));
        subtasks.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        subtasks.keySet().forEach(id -> prioritizedTasks.remove(subtasks.get(id)));
        subtasks.clear();
        //удаление всех подзадач у эпиков
        for (int i : epics.keySet()) {
            Epic epic = epics.get(i);
            epic.deleteAllSubtasks();
            updateEpicStatus(epic);
            epics.put(i, epic);
        }
    }


    //удаление по id для трех видов задач
    @Override
    public void deleteTask(int id) {
        prioritizedTasks.remove(tasks.get(id));
        tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void deleteEpic(int id) {
        epics.get(id).getSubtasksIds().forEach(subtaskId -> {
            prioritizedTasks.remove(subtasks.get(subtaskId));
            subtasks.remove(subtaskId);
            historyManager.remove(subtaskId);
        });

        epics.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void deleteSubtask(int id) {
        prioritizedTasks.remove(subtasks.get(id));
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
    public int updateTask(Task newTask) {
        if (newTask != null) {
            int id = newTask.getId();
            if (!tasks.containsKey(id)) {
                System.out.println("Такой задачи нет в списке");
                return 2;
            } else {
                if (prioritizedTasks.stream().filter(task -> !task.equals(tasks.get(id))).anyMatch(task -> isIntersects(task, newTask))) {
                    System.out.println("Обновляемая задача " + newTask.getName() + " накладывается на уже существующую. Задача не обновлена");
                    return 1;
                } else {
                    prioritizedTasks.remove(tasks.get(id));
                    prioritizedTasks.add(newTask);
                    tasks.put(id, newTask);
                }
            }
        }
        return 0;
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
                        prioritizedTasks.remove(subtasks.get(oldId));
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
    public int updateSubtask(Subtask newSubtask) {
        if (newSubtask != null) {
            int id = newSubtask.getId();
            if (!subtasks.containsKey(id)) {
                System.out.println("Такой подзадачи нет в списке");
                return 2;
            } else {
                if (prioritizedTasks.stream().filter(task -> !task.equals(subtasks.get(id))).anyMatch(task -> isIntersects(task, newSubtask))) {
                    System.out.println("Обновляемая подзадача " + newSubtask.getName() + " накладывается на уже существующую. Подзадача не обновлена");
                    return 1;
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
                    prioritizedTasks.remove(subtasks.get(id));
                    subtasks.put(id, newSubtask);
                    prioritizedTasks.add(subtasks.get(id));
                    parentEpic = updateEpicStatus(parentEpic);
                    epics.put(newParentEpicId, parentEpic);
                }
            }
        }
        return 0;
    }


    @Override
    public List<Subtask> getEpicsSubtasks(Epic epic) {
        if (epic != null) {
            ArrayList<Subtask> subtasksInEpic = new ArrayList<>();
            epic.getSubtasksIds().forEach(id -> subtasksInEpic.add(subtasks.get(id)));
            return subtasksInEpic;
        }
        return null;
    }

    @Override
    public TreeSet<Task> getPrioritizedTasks() {
        return prioritizedTasks;
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

    @Override
    public Boolean isIntersects(Task task1, Task task2) {
        if ((task2.getStartTime().isBefore(task1.getEndTime()) && task1.getStartTime().isBefore(task2.getEndTime()))
                || (task1.getStartTime().isBefore(task2.getEndTime()) && task2.getStartTime().isBefore(task1.getEndTime()))) {
            return true;
        } else {
            return false;
        }
    }


}
