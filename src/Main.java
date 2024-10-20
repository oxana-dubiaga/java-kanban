import model.Epic;
import model.Subtask;
import model.Task;
import service.FileBackedTaskManager;
import service.Managers;
import service.TaskManager;

import java.io.File;

public class Main {
    public static void main(String[] args) {

        //экземпляры разных классов для тестов
        TaskManager fileBackedTaskManager = Managers.getFileBackedTaskManager("D:\\Practicum\\java-kanban\\data.csv");

        //задачи
        Task task1 = new Task("Помыть машину", "Нужно съездить на мойку", 555);
        Task task2 = new Task("Поздравить бабушку с ДР", "70 лет", 9);

        //Эпик1 и подзадачи для него
        Epic epic1 = new Epic("Спланировать поездку в отпуск", "---", 3);
        Subtask subtask1 = new Subtask("Купить билеты", "----", 4, 3);
        Subtask subtask2 = new Subtask("Забронировать отель", "---", 5, 3);
        Subtask subtask3 = new Subtask("Сделать визу", "---", 6, 3);

        //Эпик2 и подзадачи для него
        Epic epic2 = new Epic("Подготовиться к конференции", "----", 7);
        Subtask subtask4 = new Subtask("Сделать презентацию", "--", 8, 7);

        //добавляем задачи и записываем в файл data.csv
        fileBackedTaskManager.addNewTask(task1);
        fileBackedTaskManager.addNewTask(task2);
        fileBackedTaskManager.addNewEpic(epic1);
        fileBackedTaskManager.addNewSubtask(subtask1);
        fileBackedTaskManager.addNewSubtask(subtask2);
        fileBackedTaskManager.addNewSubtask(subtask3);
        fileBackedTaskManager.addNewEpic(epic2);
        fileBackedTaskManager.addNewSubtask(subtask4);

        //создаем менеджер на основе файла dataToCreate.csv
        FileBackedTaskManager fileBackedTaskManager2 = FileBackedTaskManager.loadFromFile(new File("D:\\Practicum\\java-kanban\\dataToCreate.csv"));
        System.out.println(fileBackedTaskManager2.getAllTasks());
        System.out.println(fileBackedTaskManager2.getAllEpics());
        System.out.println(fileBackedTaskManager2.getAllSubtasks());
    }
}
