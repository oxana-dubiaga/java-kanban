import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import http.HttpTaskServer;
import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import serializerdeserializer.*;
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

public class PrioritizedHandlerTest {
    TaskManager manager = Managers.getDefault();
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson;

    @BeforeEach
    public void startServer() throws IOException {
        manager.deleteALLTasks();
        manager.deleteAllEpic();
        GsonBuilder gb = new GsonBuilder();
        gb.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(Task.class, new TaskSerializer())
                .registerTypeAdapter(Task.class, new TaskDeserializer())
                .registerTypeAdapter(Epic.class, new EpicSerializer())
                .registerTypeAdapter(Epic.class, new EpicDeserializer())
                .registerTypeAdapter(Subtask.class, new SubtaskSerializer())
                .registerTypeAdapter(Subtask.class, new SubtaskDeserializer());
        gson = gb.create();
        taskServer.startServer();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stopServer();
    }

    @Test
    public void getPrioritized() throws IOException, InterruptedException {
        Task task1 = new Task("Task1", "description", 1, 60, "21.12.2020 15:40");
        Task task2 = new Task("Task2", "description", 2, 20, "22.12.2020 15:40");
        Task task3 = new Task("Task3", "description", 3, 20, "20.12.2020 15:40");
        manager.addNewTask(task1);
        manager.addNewTask(task2);
        manager.addNewTask(task3);

        HttpClient client = HttpClient.newHttpClient();

        URI uri1 = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder().uri(uri1).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Неправильный код ответа");
        List<Task> prioritizedTasks = gson.fromJson(response.body(), new TypeToken<List<Task>>() {
        }.getType());
        assertEquals(3, prioritizedTasks.size(), "Неправильное количество задач в списке по приоритетам");
        assertEquals(task3, prioritizedTasks.get(0), "Неправильный порядок по приоритету");
        assertEquals(task1, prioritizedTasks.get(1), "Неправильный порядок по приоритету");
        assertEquals(task2, prioritizedTasks.get(2), "Неправильный порядок по приоритету");
    }
}
