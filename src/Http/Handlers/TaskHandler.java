package Http.Handlers;

import Http.SerializerDeserializer.DurationAdapter;
import Http.SerializerDeserializer.TaskDeserializer;
import Http.SerializerDeserializer.TaskSerializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.Task;
import service.TaskManager;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Pattern;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {
    private Gson gson;
    private final Pattern PATH_WITH_ID = Pattern.compile("^/tasks/\\d+$");

    public TaskHandler(TaskManager taskManager) {
        super(taskManager);
        GsonBuilder gb = new GsonBuilder();
        gb.registerTypeAdapter(LocalDateTime.class, new DurationAdapter.LocalDateTimeAdapter())
                .registerTypeAdapter(Task.class, new TaskSerializer())
                .registerTypeAdapter(Task.class, new TaskDeserializer())
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

    //обработка GET запроса - получение задачи по id (/tasks/id) или получение всех задач (/tasks)
    private void handleGetRequest(HttpExchange exchange, String path) throws IOException {
        if (PATH_WITH_ID.matcher(path).matches()) {
            String[] splitPath = path.split("/");
            int id = Integer.parseInt(splitPath[2]);
            Task task = taskManager.getTask(id);
            if (task != null) {
                try {
                    sendText(exchange, gson.toJson(task));
                } catch (IOException ex) {
                    sendInternalServerError(exchange);
                }
            } else {
                sendNotFound(exchange);
            }
        } else if (path.equals("/tasks")) {
            List<Task> tasks = taskManager.getAllTasks();
            try {
                sendText(exchange, gson.toJson(tasks));
            } catch (IOException ex) {
                sendInternalServerError(exchange);
            }
        } else {
            sendNotFound(exchange);
        }
    }

    //обработка POST запроса - добавление новой задачи или обновление существующей
    public void handlePostRequest(HttpExchange exchange, String path) throws IOException {
        if (PATH_WITH_ID.matcher(path).matches()) {
            try {
                String requestBody = readBody(exchange);
                Task updatedTask = gson.fromJson(requestBody, Task.class);
                int statusCode = taskManager.updateTask(updatedTask);
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
        } else if (path.equals("/tasks")) {
            try {
                String requestBody = readBody(exchange);
                Task newTask = gson.fromJson(requestBody, Task.class);
                int statusCode = taskManager.addNewTask(newTask);
                switch (statusCode) {
                    case 0:
                        sendSuccessUpdate(exchange);
                        break;
                    case 1:
                        sendHasInteractions(exchange);
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
                taskManager.deleteTask(id);
                sendSuccessUpdate(exchange);
            } catch (IOException ex) {
                sendInternalServerError(exchange);
            }
        } else {
            sendNotFound(exchange);
        }
    }


}
