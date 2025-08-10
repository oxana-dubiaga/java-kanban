package Handlers;

import SerializerDeserializer.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.Epic;
import model.Subtask;
import model.Task;
import service.TaskManager;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {
    private Gson gson;

    public HistoryHandler(TaskManager taskManager) {
        super(taskManager);
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
    }


    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String requestMethod = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        switch (requestMethod) {
            case "GET":
                handleGetRequest(exchange, path);
                break;
            default:
                sendNotFound(exchange);
        }
    }

    //обработка GET запроса - получение истории просмотров
    private void handleGetRequest(HttpExchange exchange, String path) throws IOException {
        if (path.equals("/history")) {
            List<Task> history = taskManager.getHistory();
            try {
                sendText(exchange, gson.toJson(history));
            } catch (IOException ex) {
                sendInternalServerError(exchange);
            }
        } else {
            sendNotFound(exchange);
        }
    }

}
