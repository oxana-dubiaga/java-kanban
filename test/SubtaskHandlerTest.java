import Http.HttpTaskServer;
import SerializerDeserializer.DurationAdapter;
import SerializerDeserializer.SubtaskDeserializer;
import SerializerDeserializer.SubtaskSerializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import model.Epic;
import model.Subtask;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

public class SubtaskHandlerTest {
    TaskManager manager = Managers.getDefault();
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson;

    @BeforeEach
    public void startServer() throws IOException {
        manager.deleteALLTasks();
        manager.deleteAllEpic();
        GsonBuilder gb = new GsonBuilder();
        gb.registerTypeAdapter(LocalDateTime.class, new DurationAdapter.LocalDateTimeAdapter())
                .registerTypeAdapter(Subtask.class, new SubtaskSerializer())
                .registerTypeAdapter(Subtask.class, new SubtaskDeserializer())
                .registerTypeAdapter(Duration.class, new DurationAdapter());
        gson = gb.create();
        taskServer.startServer();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stopServer();
    }

    @Test
    public void addSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("name", "description", 1);
        manager.addNewEpic(epic);
        Subtask subtask = new Subtask("name", "---", 2, 1, 45, "14.09.2025 22:15");
        String taskJson = gson.toJson(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Неправильный код ответа");

        Subtask testSubtask = manager.getSubtask(2);
        assertNotNull(testSubtask, "Задачи не добавляются");
        assertEquals(subtask, testSubtask, "Неправильное добавление подзадач");

        Subtask subtask1 = new Subtask("name1", "---", 3, 1, 200, "14.09.2025 22:10");
        String taskJson1 = gson.toJson(subtask1);
        HttpRequest request1 = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson1))
                .build();
        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response1.statusCode(), "Неправильный код ответа");
        List<Subtask> subtasks = manager.getAllSubtasks();
        assertEquals(1, subtasks.size(), "Добавляются подзадачи с пересечением");
    }

    @Test
    public void getSubtasks() throws IOException, InterruptedException {
        Epic epic = new Epic("name", "description", 1);
        manager.addNewEpic(epic);
        Subtask subtask2 = new Subtask("name", "---", 2, 1, 45, "14.09.2025 22:15");
        Subtask subtask3 = new Subtask("name", "---", 3, 1, 45, "15.09.2025 22:15");
        manager.addNewSubtask(subtask2);
        manager.addNewSubtask(subtask3);

        HttpClient client = HttpClient.newHttpClient();

        URI uri1 = URI.create("http://localhost:8080/subtasks");
        HttpRequest request1 = HttpRequest.newBuilder().uri(uri1).GET().build();
        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response1.statusCode(), "Неправильный код ответа");
        List<Subtask> subtasks = gson.fromJson(response1.body(), new TypeToken<List<Subtask>>() {
        }.getType());
        assertEquals(2, subtasks.size(), "Некорректное количество задач в списке");


        URI uri2 = URI.create("http://localhost:8080/subtasks/2");
        HttpRequest request2 = HttpRequest.newBuilder().uri(uri2).GET().build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response2.statusCode());
        Subtask testSubtask2 = gson.fromJson(response2.body(), Subtask.class);
        assertEquals(testSubtask2, subtask2, "Неправильное получение задачи по id");

        int taskId3 = 99;
        URI uri3 = URI.create("http://localhost:8080/subtasks" + taskId3);
        HttpRequest request3 = HttpRequest.newBuilder().uri(uri3).GET().build();
        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response3.statusCode(), "Неправильный код ответа");
    }

    @Test
    public void deleteSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("name", "description", 1);
        manager.addNewEpic(epic);
        Subtask subtask2 = new Subtask("name", "---", 2, 1, 45, "14.09.2025 22:15");
        manager.addNewSubtask(subtask2);

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/subtasks/2");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Неправильный код ответа");
        List<Subtask> subtasks = manager.getAllSubtasks();
        assertEquals(0, subtasks.size(), "Задачи не удаляются");
    }


}
