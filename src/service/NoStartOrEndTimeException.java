package service;

public class NoStartOrEndTimeException extends NullPointerException {
    public NoStartOrEndTimeException(final String massage) {
        super(massage);
    }
}
