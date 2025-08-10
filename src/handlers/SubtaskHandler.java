package handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.Subtask;
import serializerdeserializer.DurationAdapter;
import serializerdeserializer.SubtaskDeserializer;
import serializerdeserializer.SubtaskSerializer;
import service.TaskManager;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Pattern;

public class SubtaskHandler extends BaseHttpHandler implements HttpHandler {
    private Gson gson;
    private final Pattern PATH_WITH_ID = Pattern.compile("^/subtasks/\\d+$");

    public SubtaskHandler(TaskManager taskManager) {
        super(taskManager);
        GsonBuilder gb = new GsonBuilder();
        gb.registerTypeAdapter(LocalDateTime.class, new DurationAdapter.LocalDateTimeAdapter())
                .registerTypeAdapter(Subtask.class, new SubtaskSerializer())
                .registerTypeAdapter(Subtask.class, new SubtaskDeserializer())
                .registerTypeAdapter(Duration.class, new DurationAdapter());
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
            case "POST":
                handlePostRequest(exchange, path);
                break;
            case "DELETE":
                handleDeleteRequest(exchange, path);
                break;
            default:
                sendNotFound(exchange);
        }
    }

    //обработка GET запроса - получение задачи по id (/subtasks/id) или получение всех задач (/subtasks)
    private void handleGetRequest(HttpExchange exchange, String path) throws IOException {
        if (PATH_WITH_ID.matcher(path).matches()) {
            String[] splitPath = path.split("/");
            int id = Integer.parseInt(splitPath[2]);
            Subtask subtask = taskManager.getSubtask(id);
            if (subtask != null) {
                try {
                    sendText(exchange, gson.toJson(subtask));
                } catch (IOException ex) {
                    sendInternalServerError(exchange);
                }
            } else {
                sendNotFound(exchange);
            }
        } else if (path.equals("/subtasks")) {
            List<Subtask> subtasks = taskManager.getAllSubtasks();
            try {
                sendText(exchange, gson.toJson(subtasks));
            } catch (IOException ex) {
                sendInternalServerError(exchange);
            }
        } else {
            sendNotFound(exchange);
        }
    }

    //обработка POST запроса - добавление новой подзадачи или обновление существующей
    private void handlePostRequest(HttpExchange exchange, String path) throws IOException {
        if (PATH_WITH_ID.matcher(path).matches()) {
            try {
                String requestBody = readBody(exchange);
                Subtask updatedSubtask = gson.fromJson(requestBody, Subtask.class);
                int statusCode = taskManager.updateSubtask(updatedSubtask);
                switch (statusCode) {
                    case 0:
                        sendSuccessUpdate(exchange);
                        break;
                    case 1:
                        sendHasInteractions(exchange);
                        break;
                    case 2:
                        sendNotFound(exchange);
                        break;
                }
            } catch (IOException ex) {
                sendInternalServerError(exchange);
            }
        } else if (path.equals("/subtasks")) {
            try {
                String requestBody = readBody(exchange);
                Subtask newSubtask = gson.fromJson(requestBody, Subtask.class);
                int statusCode = taskManager.addNewSubtask(newSubtask);
                switch (statusCode) {
                    case 0:
                        sendSuccessUpdate(exchange);
                        break;
                    case 1:
                        sendHasInteractions(exchange);
                        break;
                    case 2:
                        sendNotFound(exchange);
                        break;
                }
            } catch (IOException ex) {
                sendInternalServerError(exchange);
            }
        } else {
            sendNotFound(exchange);
        }
    }

    //обработка DELETE запроса
    private void handleDeleteRequest(HttpExchange exchange, String path) throws IOException {
        if (PATH_WITH_ID.matcher(path).matches()) {
            try {
                String[] splitPath = path.split("/");
                int id = Integer.parseInt(splitPath[2]);
                taskManager.deleteSubtask(id);
                sendSuccessUpdate(exchange);
            } catch (IOException ex) {
                sendInternalServerError(exchange);
            }
        } else {
            sendNotFound(exchange);
        }
    }


}
