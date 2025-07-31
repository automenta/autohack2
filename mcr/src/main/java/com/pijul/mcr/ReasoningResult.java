package com.pijul.mcr;

import java.util.List;

public class ReasoningResult {
    private final String answer;
    private final List<String> history;

    public ReasoningResult(String answer, List<String> history) {
        this.answer = answer;
        this.history = history;
    }

    public String getAnswer() {
        return answer;
    }

    public List<String> getHistory() {
        return history;
    }
}
