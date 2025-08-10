import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import http.HttpTaskServer;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import serializerdeserializer.DurationAdapter;
import serializerdeserializer.TaskDeserializer;
import serializerdeserializer.TaskSerializer;
import service.Managers;
import service.TaskManager;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TaskHandlerTest {

    TaskManager manager = Managers.getDefault();
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson;

    @BeforeEach
    public void startServer() throws IOException {
        manager.deleteALLTasks();
        manager.deleteAllEpic();
        GsonBuilder gb = new GsonBuilder();
        gb.registerTypeAdapter(LocalDateTime.class, new DurationAdapter.LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(Task.class, new TaskSerializer())
                .registerTypeAdapter(Task.class, new TaskDeserializer());
        gson = gb.create();
        taskServer.startServer();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stopServer();
    }


    @Test
    public void addTask() throws IOException, InterruptedException {
        int taskId = 1;
        Task task = new Task("Task", "description", taskId, 60, "20.12.2020 15:40");
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Неправильный код ответа");
        Task testTask = manager.getTask(taskId);
        assertNotNull(testTask, "Задачи не добавляются");
        assertEquals(testTask, task, "Неправильное добавление задач");


        int taskId2 = 2;
        Task task2 = new Task("Task2", "description", taskId2, 120, "20.12.2020 15:30");
        String taskJson2 = gson.toJson(task2);
        HttpRequest request2 = HttpRequest.newBuilder().uri(uri).POST(HttpRequest.BodyPublishers.ofString(taskJson2)).build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response2.statusCode(), "Неправильный код ответа");
        List<Task> tasks = manager.getAllTasks();
        assertEquals(1, tasks.size(), "Добавляются задачи с пересечением");

    }

    @Test
    public void getTasks() throws IOException, InterruptedException {
        int taskId1 = 1;
        int taskId2 = 2;
        Task task1 = new Task("Task1", "description", taskId1, 60, "20.12.2020 15:40");
        Task task2 = new Task("Task2", "description", taskId2, 20, "22.12.2020 15:40");

        manager.addNewTask(task1);
        manager.addNewTask(task2);

        HttpClient client = HttpClient.newHttpClient();

        URI uri1 = URI.create("http://localhost:8080/tasks");
        HttpRequest request1 = HttpRequest.newBuilder().uri(uri1).GET().build();
        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response1.statusCode(), "Неправильный код ответа");
        List<Task> tasks = gson.fromJson(response1.body(), new TypeToken<List<Task>>() {
        }.getType());
        assertEquals(2, tasks.size(), "Некорректное количество задач в списке");


        URI uri2 = URI.create("http://localhost:8080/tasks/" + taskId1);
        HttpRequest request2 = HttpRequest.newBuilder().uri(uri2).GET().build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response2.statusCode(), "Неправильный код ответа");
        Task testTask1 = gson.fromJson(response2.body(), Task.class);
        assertEquals(testTask1, task1, "Неправильное получение задачи по id");

        int taskId3 = 99;
        URI uri3 = URI.create("http://localhost:8080/tasks/" + taskId3);
        HttpRequest request3 = HttpRequest.newBuilder().uri(uri3).GET().build();
        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response3.statusCode(), "Неправильный код ответа");
    }

    @Test
    public void deleteTask() throws IOException, InterruptedException {
        int taskId = 1;
        Task task = new Task("Task", "description", taskId, 60, "20.12.2020 15:40");
        manager.addNewTask(task);

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/" + taskId);
        HttpRequest request = HttpRequest.newBuilder().uri(uri).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Неправильный код ответа");
        List<Task> tasks = manager.getAllTasks();
        assertEquals(0, tasks.size(), "Задачи не удаляются");
    }


}
