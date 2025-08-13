package http;

import com.sun.net.httpserver.HttpServer;
import handlers.*;
import service.Managers;
import service.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private final int port = 8080;
    private HttpServer server;
    private TaskManager taskManager;

    public HttpTaskServer(TaskManager taskManager) {
        this.taskManager = taskManager;
    }


    public void startServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);

        server.createContext("/tasks", new TaskHandler(taskManager));
        server.createContext("/subtasks", new SubtaskHandler(taskManager));
        server.createContext("/epics", new EpicHandler(taskManager));
        server.createContext("/history", new HistoryHandler(taskManager));
        server.createContext("/prioritized", new PrioritizedHandler(taskManager));

        server.start();
        System.out.println("Сервер запущен на порту " + port);
    }

    public void stopServer() {
        if (server != null) {
            server.stop(0);
        }
        System.out.println("Сервер остановлен");
    }

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();
        HttpTaskServer newServer = new HttpTaskServer(taskManager);
        try {
            newServer.startServer();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
