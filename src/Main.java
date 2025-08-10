import model.Epic;
import model.Status;
import model.Subtask;
import service.Managers;
import service.TaskManager;

public class Main {
    public static void main(String[] args) {
//
//        //экземпляры разных классов для тестов
//        TaskManager fileBackedTaskManager = Managers.getFileBackedTaskManager("D:\\Practicum\\java-kanban\\data.csv");
//
//        //задачи
//        Task task1 = new Task("Помыть машину", "Нужно съездить на мойку", 555, 180, "20.07.2025 14:30");
//        Task task2 = new Task("Поздравить бабушку с ДР", "70 лет", 9, 15, "25.08.2025 11:00");
//
//        //Эпик1 и подзадачи для него
//        Epic epic1 = new Epic("Спланировать поездку в отпуск", "---", 3);
//        Subtask subtask1 = new Subtask("Купить билеты", "----", 4, 3, 10, "21.07.2025 14:00");
//        Subtask subtask2 = new Subtask("Забронировать отель", "---", 5, 3, 40, "21.07.2025 12:20");
//        Subtask subtask3 = new Subtask("Сделать визу", "---", 6, 3, 120, "25.07.2025 12:00");
//
//        //Эпик2 и подзадачи для него
//        Epic epic2 = new Epic("Подготовиться к конференции", "----", 7);
//        Subtask subtask4 = new Subtask("Сделать презентацию", "--", 8, 7, 60, "01.08.2025 14:00");
//
//        //Задачи, которые будут иметь пересечение по времени с существующими
//        Task task3 = new Task("task3", "description3 ", 11, 120, "20.07.2025 13:30"); //пересечение с task1
//        Subtask subtask5 = new Subtask("subtask5", "description5", 12, 3, 120, "25.08.2025 11:10"); //пересечение с task2
//
//
//        //добавляем задачи и записываем в файл data.csv
//        fileBackedTaskManager.addNewTask(task1);
//        fileBackedTaskManager.addNewTask(task2);
//        fileBackedTaskManager.addNewEpic(epic1);
//        fileBackedTaskManager.addNewSubtask(subtask1);
//        fileBackedTaskManager.addNewSubtask(subtask2);
//        fileBackedTaskManager.addNewSubtask(subtask3);
//        fileBackedTaskManager.addNewEpic(epic2);
//        fileBackedTaskManager.addNewSubtask(subtask4);
//        //добавляются задачи с пересечением
//        fileBackedTaskManager.addNewTask(task3);
//        fileBackedTaskManager.addNewSubtask(subtask5);
//
//        //проверка расчета времени старта/финиша и продолжительности у эпика
//        System.out.println("эпик 1 с тремя задачами");
//        System.out.println(epic1.getStartTime());
//        System.out.println(epic1.getDuration());
//        System.out.println(epic1.getEndTime());
//        System.out.println("Вывод задач по приоритету");
//        System.out.println(fileBackedTaskManager.getPrioritizedTasks());
//        System.out.println();
//        System.out.println("эпик 1 после удаления первой");
//        fileBackedTaskManager.deleteSubtask(5);
//        System.out.println(epic1.getSubtasksIds());
//        System.out.println(epic1.getStartTime());
//        System.out.println(epic1.getDuration());
//        System.out.println();
//        System.out.println("Вывод задач по приоритету после удаления");
//        System.out.println(fileBackedTaskManager.getPrioritizedTasks());
//        System.out.println();
//
//        //создаем менеджер на основе файла dataToCreate.csv
//        FileBackedTaskManager fileBackedTaskManager2 = FileBackedTaskManager.loadFromFile(new File("D:\\Practicum\\java-kanban\\dataToCreate.csv"));
//        System.out.println(fileBackedTaskManager2.getAllTasks());
//        System.out.println(fileBackedTaskManager2.getAllEpics());
//        System.out.println(fileBackedTaskManager2.getAllSubtasks());
//        System.out.println(fileBackedTaskManager2.getEpic(2).getStartTime());
//        System.out.println(fileBackedTaskManager2.getEpic(2).getEndTime());
//        System.out.println(fileBackedTaskManager2.getEpic(2).getDuration());
//
//        System.out.println();
//        System.out.println(fileBackedTaskManager2.getPrioritizedTasks());


        TaskManager tm = Managers.getDefault();
        //создаем и добавляем эпик
        Epic epic1 = new Epic("Спланировать поездку в отпуск", "---", 1);
        tm.addNewEpic(epic1);
        //создаем и добавляем подзадачу в эпике 1
        Subtask subtask = new Subtask("Купить билеты", "----", 2, 1, 10, "21.07.2025 14:00");
        subtask.setStatus(Status.IN_PROGRESS);
        tm.addNewSubtask(subtask);
        //проверка, что подзадача добавилась к эпику
        System.out.println("Подзадачи первого эпика - должна быть подзадача айди2, статус - ин прогрес");
        System.out.println(tm.getAllEpics());
        //создаем задачу для обновления - с тем же айди, но меняем у подзадачи айди эпика и обновляем - должен создасться новый эпик
        Subtask subtaskUpd = new Subtask("upd", "----", 2, 3, 10, "21.07.2025 14:00");
        tm.updateSubtask(subtaskUpd);
        System.out.println("Эпики после обновления подзадачи: Подзадачи первого эпика - должны быть пустыми, статус нью ");
        System.out.println("Подзадачи нового эпика - должна быть обновленная подзадача с айди 2, статус - ин прогрес");
        System.out.println(tm.getAllEpics());


    }
}
