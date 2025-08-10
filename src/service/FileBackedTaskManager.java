package service;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {

    String filePath;

    public FileBackedTaskManager(HistoryManager historyManager, String path) {
        super(historyManager);
        filePath = path;
    }


    @Override
    public int addNewTask(Task newTask) {
        int statusCode  = super.addNewTask(newTask);
        save();
        return statusCode;
    }

    @Override
    public void addNewEpic(Epic newEpic) {
        super.addNewEpic(newEpic);
        save();
    }

    @Override
    public int addNewSubtask(Subtask newSubtask) {
        int statusCode = super.addNewSubtask(newSubtask);
        save();
        return statusCode;
    }

    @Override
    public void deleteALLTasks() {
        super.deleteALLTasks();
        save();
    }

    @Override
    public void deleteAllEpic() {
        super.deleteAllEpic();
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        save();
    }

    @Override
    public void deleteSubtask(int id) {
        super.deleteSubtask(id);
        save();
    }

    @Override
    public int updateTask(Task newTask) {
        int statusCode = super.updateTask(newTask);
        save();
        return statusCode;
    }

    @Override
    public void updateEpic(Epic newEpic) {
        super.updateEpic(newEpic);
        save();
    }

    @Override
    public int updateSubtask(Subtask newSubtask) {
        int statusCode = super.updateSubtask(newSubtask);
        save();
        return statusCode;
    }


    private void save() {
        List<Task> allTasks = getAllTasks();
        List<Epic> allEpics = getAllEpics();
        List<Subtask> allSubtasks = getAllSubtasks();

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            for (Task task : allTasks) {
                bw.write(task.toStringInFile() + "\n");
            }
            for (Epic epic : allEpics) {
                bw.write(epic.toStringInFile() + "\n");
            }
            for (Subtask subtask : allSubtasks) {
                bw.write(subtask.toStringInFile() + "\n");
            }
        } catch (IOException ex) {
            throw new ManagerSaveException("Ошибка при сохранении в файл " + filePath);
        }

    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager fileBackedTaskManager = Managers.getFileBackedTaskManager(file.getPath());
        List<String> allTypesTasksFromFile;
        try {
            allTypesTasksFromFile = Files.readAllLines(file.toPath());
        } catch (IOException ex) {
            throw new ManagerSaveException("Ошибка при чтении из файла " + file.getPath());
        }

        for (String line : allTypesTasksFromFile) {
            String[] splitLine = line.split(",");

            if (!(splitLine.length == 5 || splitLine.length == 7 || splitLine.length == 8)) {
                System.out.println("Строка " + line + " не соответствует требуемому виду. Данные из строки не записаны.");
                continue;
            }


            if (splitLine[1].equalsIgnoreCase("Task")) {
                Task task = new Task(splitLine[2], splitLine[4], Integer.parseInt(splitLine[0]), Integer.parseInt(splitLine[6]), splitLine[5]);
                try {
                    task = setStatusFromFile(task, splitLine[3]);
                    fileBackedTaskManager.addNewTask(task);
                } catch (TaskStatusException ex) {
                    System.out.println("В строке " + line + " статус не соответствует требуемому виду. Данные из строки не записаны.");
                }
            } else if (splitLine[1].equalsIgnoreCase("Epic")) {
                Epic epic = new Epic(splitLine[2], splitLine[4], Integer.parseInt(splitLine[0]));
                try {
                    epic = setStatusFromFile(epic, splitLine[3]);
                    fileBackedTaskManager.addNewEpic(epic);
                } catch (TaskStatusException ex) {
                    System.out.println("В строке " + line + " статус не соответствует требуемому виду. Данные из строки не записаны.");
                }
            } else if (splitLine[1].equalsIgnoreCase("Subtask")) {
                Subtask subtask = new Subtask(splitLine[2], splitLine[4], Integer.parseInt(splitLine[0]), Integer.parseInt(splitLine[5]), Integer.parseInt(splitLine[7]), splitLine[6]);
                try {
                    subtask = setStatusFromFile(subtask, splitLine[3]);
                    fileBackedTaskManager.addNewSubtask(subtask);
                } catch (TaskStatusException ex) {
                    System.out.println("В строке " + line + " статус не соответствует требуемому виду. Данные из строки не записаны.");
                }
            } else {
                System.out.println("Строка " + line + " не соответствует требуемому виду. Данные из строки не записаны.");
            }
        }

        return fileBackedTaskManager;
    }


    private static <T extends Task> T setStatusFromFile(T task, String status) throws TaskStatusException {
        switch (status.toLowerCase()) {
            case ("new"):
                task.setStatus(Status.NEW);
                break;
            case ("in_progress"):
                task.setStatus(Status.IN_PROGRESS);
                break;
            case ("done"):
                task.setStatus(Status.DONE);
                break;
            default:
                throw new TaskStatusException("Некорректный статус задачи");
        }
        return task;
    }


}
