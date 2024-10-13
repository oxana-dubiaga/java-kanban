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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {

    String filePath;

    public FileBackedTaskManager(HistoryManager historyManager, String path) {
        super(historyManager);
        filePath = path;
    }


    @Override
    public void addNewTask(Task newTask) {
        super.addNewTask(newTask);
        save();
    }

    @Override
    public void addNewEpic(Epic newEpic) {
        super.addNewEpic(newEpic);
        save();
    }

    @Override
    public void addNewSubtask(Subtask newSubtask) {
        super.addNewSubtask(newSubtask);
        save();
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
    public void updateTask(Task newTask) {
        super.updateTask(newTask);
        save();
    }

    @Override
    public void updateEpic(Epic newEpic) {
        super.updateEpic(newEpic);
        save();
    }

    @Override
    public void updateSubtask(Subtask newSubtask) {
        super.updateSubtask(newSubtask);
        save();
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
            throw new ManagerSaveException("Ошибка при сохранении в файл");
        }

    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager fileBackedTaskManager = Managers.getFileBackedTaskManager(file.getPath());
        List<String> allTypesTasksFromFile;
        try {
            allTypesTasksFromFile = Files.readAllLines(file.toPath());
        } catch (IOException ex) {
            throw new ManagerSaveException("Ошибка при чтении из файла");
        }

        for (String line : allTypesTasksFromFile) {
            String[] splitLine = line.split(",");

            if (!(splitLine.length == 5 || splitLine.length == 6)) {
                System.out.println("Строка " + line + " не соответствует требуемому виду. Данные из строки не записаны.");
                continue;
            }

            if (splitLine.length == 5) {
                if (splitLine[1].equalsIgnoreCase("Task")) {
                    Task task = new Task(splitLine[2], splitLine[4], Integer.parseInt(splitLine[0]));
                    task = setStatusFromFile(task, splitLine[3]);
                    fileBackedTaskManager.addNewTask(task);
                } else if (splitLine[1].equalsIgnoreCase("Epic")) {
                    Epic epic = new Epic(splitLine[2], splitLine[4], Integer.parseInt(splitLine[0]));
                    epic = setStatusFromFile(epic, splitLine[3]);
                    fileBackedTaskManager.addNewEpic(epic);
                }
            } else if (splitLine[1].equalsIgnoreCase("Subtask")) {
                Subtask subtask = new Subtask(splitLine[2], splitLine[4], Integer.parseInt(splitLine[0]), Integer.parseInt(splitLine[5]));
                subtask = setStatusFromFile(subtask, splitLine[3]);
                fileBackedTaskManager.addNewSubtask(subtask);
            } else {
                System.out.println("Строка " + line + " не соответствует требуемому виду. Данные из строки не записаны.");
            }
        }

        return fileBackedTaskManager;
    }


    private static <T extends Task> T setStatusFromFile(T task, String status) {
        if (status.equalsIgnoreCase("New")) {
            task.setStatus(Status.NEW);
        } else if (status.equalsIgnoreCase("IN_PROGRESS")) {
            task.setStatus(Status.IN_PROGRESS);
        } else if (status.equalsIgnoreCase("Done")) {
            task.setStatus(Status.DONE);
        } else {
            task.setStatus(Status.NEW);
        }
        return task;
    }


}
