package dumb.mcr.exceptions;

public class ToolExecutionException extends RuntimeException {
    public ToolExecutionException(String message) {
        super(message);
    }

    public ToolExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
}
