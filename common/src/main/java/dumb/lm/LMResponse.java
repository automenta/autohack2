package dumb.lm;

public record LMResponse(String content, LMUsage usage, boolean success, String error) {
}
