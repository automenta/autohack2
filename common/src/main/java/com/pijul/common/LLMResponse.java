package com.pijul.common;

public class LLMResponse {
    private String content;
    private LLMUsage usage;
    private boolean success;
    private String error;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LLMUsage getUsage() {
        return usage;
    }

    public void setUsage(LLMUsage usage) {
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
