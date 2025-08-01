package dumb.lm;

public class LMResponse {
    private final boolean success;
    private final String content;
    private final String error;

    private LMResponse(boolean success, String content, String error) {
        this.success = success;
        this.content = content;
        this.error = error;
    }

    public static LMResponse success(String content) {
        return new LMResponse(true, content, null);
    }

    public static LMResponse error(String error) {
        return new LMResponse(false, null, error);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getContent() {
        return content;
    }

    public String getError() {
        return error;
    }
}
