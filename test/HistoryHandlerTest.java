import Http.HttpTaskServer;
import SerializerDeserializer.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import model.Epic;
import model.Subtask;
import model.Task;
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

public class HistoryHandlerTest {
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
    public void getHistory() throws IOException, InterruptedException {
        Task task1 = new Task("Task1", "description", 1, 60, "20.12.2020 15:40");
        Task task2 = new Task("Task2", "description", 2, 20, "22.12.2020 15:40");
        manager.addNewTask(task1);
        manager.addNewTask(task2);

        HttpClient client = HttpClient.newHttpClient();

        URI uriTask1 = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request1 = HttpRequest.newBuilder().uri(uriTask1).GET().build();
        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        URI uriTask2 = URI.create("http://localhost:8080/tasks/2");
        HttpRequest request2 = HttpRequest.newBuilder().uri(uriTask2).GET().build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

        URI uri1 = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder().uri(uri1).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Неправильный код ответа");
        List<Task> history = gson.fromJson(response.body(), new TypeToken<List<Task>>(){}.getType());
        assertEquals(2, history.size(), "Неправильно заполняется история");
    }


}
