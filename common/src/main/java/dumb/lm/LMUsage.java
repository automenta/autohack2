package dumb.lm;

public record LMUsage(long promptTokens, long completionTokens, long totalTokens) {
}
