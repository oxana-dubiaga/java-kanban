package service;

public class TaskStatusException extends RuntimeException {
    public TaskStatusException(final String massage) {
        super(massage);
    }
}
