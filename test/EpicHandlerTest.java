import Http.HttpTaskServer;
import Http.SerializerDeserializer.DurationAdapter;
import Http.SerializerDeserializer.EpicDeserializer;
import Http.SerializerDeserializer.EpicSerializer;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class EpicHandlerTest {
    TaskManager manager = Managers.getDefault();
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson;

    @BeforeEach
    public void startServer() throws IOException {
        manager.deleteALLTasks();
        manager.deleteAllEpic();
        GsonBuilder gb = new GsonBuilder();
        gb.registerTypeAdapter(LocalDateTime.class, new DurationAdapter.LocalDateTimeAdapter())
                .registerTypeAdapter(Epic.class, new EpicSerializer())
                .registerTypeAdapter(Epic.class, new EpicDeserializer())
                .registerTypeAdapter(Duration.class, new DurationAdapter());
        gson = gb.create();
        taskServer.startServer();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stopServer();
    }

    @Test
    public void addEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("name", "description", 1);
        String epicJson = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Неправильный код ответа");
        Task testEpic = manager.getEpic(epic.getId());
        assertNotNull(testEpic, "Эпики не добавляются");
        assertEquals(epic, testEpic, "Неправильное добавление эпика");
    }

    @Test
    public void getEpics() throws IOException, InterruptedException {
        Epic epic1 = new Epic("name", "description", 1);
        Epic epic2 = new Epic("name", "description", 2);

        manager.addNewEpic(epic1);
        manager.addNewEpic(epic2);


        HttpClient client = HttpClient.newHttpClient();

        URI uri1 = URI.create("http://localhost:8080/epics");
        HttpRequest request1 = HttpRequest.newBuilder().uri(uri1).GET().build();
        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response1.statusCode(), "Неправильный код ответа");
        List<Epic> epics = gson.fromJson(response1.body(), new TypeToken<List<Epic>>() {
        }.getType());
        assertEquals(2, epics.size(), "Некорректное количество эпиков в списке");


        URI uri2 = URI.create("http://localhost:8080/epics/1");
        HttpRequest request2 = HttpRequest.newBuilder().uri(uri2).GET().build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response2.statusCode(), "Неправильный код ответа");
        Epic testepic1 = gson.fromJson(response2.body(), Epic.class);
        assertEquals(epic1, testepic1, "Неправильное получение эпика по id");

        int epicId3 = 99;
        URI uri3 = URI.create("http://localhost:8080/epics/" + epicId3);
        HttpRequest request3 = HttpRequest.newBuilder().uri(uri3).GET().build();
        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response3.statusCode(), "Неправильный код ответа");
    }

    @Test
    public void deleteEpic() throws IOException, InterruptedException {
        Epic epic1 = new Epic("name", "description", 1);
        manager.addNewEpic(epic1);

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/epics/1");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Неправильный код ответа");
        List<Epic> epics = manager.getAllEpics();
        assertEquals(0, epics.size(), "Задачи не удаляются");
    }

    @Test
    public void getEpicsSubtasks() throws IOException, InterruptedException {
        Epic epic1 = new Epic("name", "description", 1);
        manager.addNewEpic(epic1);
        Subtask subtask2 = new Subtask("name", "---", 2, 1, 45, "14.09.2025 22:15");
        manager.addNewSubtask(subtask2);
        Subtask subtask3 = new Subtask("name", "---", 2, 1, 45, "13.09.2025 22:15");
        manager.addNewSubtask(subtask3);

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/epics/1/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Неправильный код ответа");
        List<Subtask> subtasks = gson.fromJson(response.body(), new TypeToken<List<Subtask>>() {
        }.getType());
        assertEquals(2, subtasks.size(), "Не добавляются подзадачи у эпика");
        assertEquals(subtask2, subtasks.get(0), "Неправильно добавляется подзадача у эпика");
    }


}
