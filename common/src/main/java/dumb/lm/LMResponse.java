package dumb.lm;

public class LMResponse {
    private String content;
    private LMUsage usage;
    private boolean success;
    private String error;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LMUsage getUsage() {
        return usage;
    }

    public void setUsage(LMUsage usage) {
        this.usage = usage;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
