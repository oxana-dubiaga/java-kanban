package Handlers;

import SerializerDeserializer.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.Epic;
import model.Subtask;
import service.TaskManager;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Pattern;

public class EpicHandler extends BaseHttpHandler implements HttpHandler {
    private Gson gson;
    private final Pattern PATH_WITH_ID = Pattern.compile("^/epics/\\d+$");

    public EpicHandler(TaskManager taskManager) {
        super(taskManager);
        GsonBuilder gb = new GsonBuilder();
        gb.registerTypeAdapter(LocalDateTime.class, new DurationAdapter.LocalDateTimeAdapter())
                .registerTypeAdapter(Epic.class, new EpicSerializer())
                .registerTypeAdapter(Epic.class, new EpicDeserializer())
                .registerTypeAdapter(Subtask.class, new SubtaskSerializer())
                .registerTypeAdapter(Subtask.class, new SubtaskDeserializer())
                .registerTypeAdapter(java.time.Duration.class, new DurationAdapter());
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

    //обработка GET запроса - получение эпика по id (/epics/id), получение всех эпиков (/epics), 
    //получение подзадач у эпика (/epics/id/subtasks)
    private void handleGetRequest(HttpExchange exchange, String path) throws IOException {
        if (PATH_WITH_ID.matcher(path).matches()) {
            String[] splitPath = path.split("/");
            int id = Integer.parseInt(splitPath[2]);
            Epic epic = taskManager.getEpic(id);
            if (epic != null) {
                try {
                    sendText(exchange, gson.toJson(epic));
                } catch (IOException ex) {
                    sendInternalServerError(exchange);
                }
            } else {
                sendNotFound(exchange);
            }
        } else if (path.equals("/epics")) {
            List<Epic> epics = taskManager.getAllEpics();
            try {
                sendText(exchange, gson.toJson(epics));
            } catch (IOException ex) {
                sendInternalServerError(exchange);
            }
        } else if (Pattern.matches("^/epics/\\d+/subtasks$", path)) {
            String[] splitPath = path.split("/");
            int id = Integer.parseInt(splitPath[2]);
            Epic epic = taskManager.getEpic(id);
            if (epic != null) {
                List<Subtask> subtasks = taskManager.getEpicsSubtasks(epic);
                sendText(exchange, gson.toJson(subtasks));
            } else {
                sendNotFound(exchange);
            }
        } else {
            sendNotFound(exchange);
        }
    }

    //обработка POST запроса - добавление нового эпика
    private void handlePostRequest(HttpExchange exchange, String path) throws IOException {
        if (path.equals("/epics")) {
            try {
                String requestBody = readBody(exchange);
                Epic newEpic = gson.fromJson(requestBody, Epic.class);
                taskManager.addNewEpic(newEpic);
                sendSuccessUpdate(exchange);
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
                taskManager.deleteEpic(id);
                sendSuccessUpdate(exchange);
            } catch (IOException ex) {
                sendInternalServerError(exchange);
            }
        } else {
            sendNotFound(exchange);
        }
    }


}