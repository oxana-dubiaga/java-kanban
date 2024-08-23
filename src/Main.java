import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import service.*;

public class Main {
    public static void main(String[] args) {

        //экземпляры разных классов для тестов
        TaskManager taskManager = Managers.getDefault();

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

        taskManager.addNewTask(task1);
        taskManager.addNewTask(task2);
        taskManager.addNewEpic(epic1);
        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask2);
        taskManager.addNewSubtask(subtask3);
        taskManager.addNewEpic(epic2);
        taskManager.addNewSubtask(subtask4);

        // проверка получения всех задач
        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllEpics());
        System.out.println(taskManager.getAllSubtasks());

        //проверка обновления задачи
        task2.setDescription("75 лет");
        taskManager.updateTask(task2);
        System.out.println(taskManager.getTask(2));
        //проверка работы с историей
        System.out.println("История");
        System.out.println(taskManager.getHistory());


        //проверка обновления подзадачи и смены статусов у эпика при изменениях подзадач
        subtask2.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubtask(subtask2);
        System.out.println(taskManager.getEpic(3).getStatus());
        //проверка работы с историей
        System.out.println("История");
        System.out.println(taskManager.getHistory());

        //проверка удаления и добавления подзадач и смены статусов у эпика при изменениях подзадач
        taskManager.deleteSubtask(6);
        System.out.println(taskManager.getEpic(3));
        subtask1.setStatus(Status.DONE);
        subtask2.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask1);
        taskManager.updateSubtask(subtask2);
        System.out.println(taskManager.getEpic(3).getStatus());
        taskManager.addNewSubtask(subtask3);
        System.out.println(taskManager.getEpic(3).getStatus());

        //проверка получения задачи с несуществующим id
        System.out.println(taskManager.getTask(100));
        System.out.println(taskManager.getEpic(100));
        System.out.println(taskManager.getSubtask(100));

        //проверка изменения принадлжености подзадачи
        System.out.println(taskManager.getEpic(3));
        System.out.println(taskManager.getEpic(7));
        System.out.println("-----------");
        Subtask subtask5 = new Subtask("Сделать визу", "---", 9, 7); // с тем жу id как сейчас у subtask3, но принадлежит другому эпику. Должна заменить subtask 3 (сравнение в equals по id)
        subtask5.setStatus(Status.IN_PROGRESS); //меняем статус, чтобы у нового родительского эпика поменялся с new на in progress
        taskManager.updateSubtask(subtask5);
        Subtask subtask6 = new Subtask("Купить билеты на самолет", "----", 4, 3); //замена в том же эпике
        taskManager.updateSubtask(subtask6);
        System.out.println(taskManager.getEpic(3));
        System.out.println(taskManager.getEpicsSubtasks(taskManager.getEpic(3)));
        System.out.println(taskManager.getEpic(7));
        System.out.println(taskManager.getEpicsSubtasks(taskManager.getEpic(7)));
        System.out.println("-----------");


        //проверка удаления эпика
        taskManager.deleteEpic(3);
        System.out.println(taskManager.getAllEpics());
        System.out.println(taskManager.getAllSubtasks());

        //проверка работы с историей
        //самый первый просмотр (task id2) должен пропасть
        System.out.println("История");
        System.out.println(taskManager.getHistory());

        Epic epic15 = new Epic("fff", "qqq", 55);


    }
}
