package dumb.lm;

public record LMResponse(boolean success, String content, String error) {

    public static LMResponse success(String content) {
        return new LMResponse(true, content, null);
    }

    public static LMResponse error(String error) {
        return new LMResponse(false, null, error);
    }
}
