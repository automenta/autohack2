package dumb.code.versioning;

public class PijulException extends RuntimeException {
    public PijulException(String message) {
        super(message);
    }

    public PijulException(String message, Throwable cause) {
        super(message, cause);
    }
}
