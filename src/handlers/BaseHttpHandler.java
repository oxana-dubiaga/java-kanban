package handlers;

import com.sun.net.httpserver.HttpExchange;
import service.TaskManager;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class BaseHttpHandler {

    protected TaskManager taskManager;

    public BaseHttpHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }


    protected void sendText(HttpExchange exchange, String text) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(200, resp.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(resp);
        }
    }

    protected void sendNotFound(HttpExchange exchange) throws IOException {
        String jsonErrorString = "{\"error\":\"Запрашиваемый адрес не существует.\"}";
        byte[] resp = jsonErrorString.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(404, resp.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(resp);
        }
    }

    protected void sendHasInteractions(HttpExchange exchange) throws IOException {
        String jsonErrorString = "{\"error\":\"Добавляемая задача накладывается по времени на уже существующие. Задача на добавлена\"}";
        byte[] resp = jsonErrorString.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(406, resp.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(resp);
        }
    }

    protected void sendInternalServerError(HttpExchange exchange) throws IOException {
        String jsonErrorString = "{\"error\":\"Внутрення ошибка сервера\"}";
        byte[] resp = jsonErrorString.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(500, resp.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(resp);
        }
    }

    protected void sendSuccessUpdate(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(201, 0);
        exchange.getResponseBody().close();
    }

    protected String readBody(HttpExchange exchange) throws IOException {
        return new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
    }

}
