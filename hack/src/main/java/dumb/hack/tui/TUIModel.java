package dumb.hack.tui;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class TUIModel {

    private String codeOutput = "Welcome to the Code view!";
    private List<String> mcrResults = new ArrayList<>();
    private List<String> knowledgeBase = new ArrayList<>();

    private Consumer<String> codeOutputListener;
    private Consumer<List<String>> mcrResultsListener;
    private Consumer<List<String>> knowledgeBaseListener;

    public void setCodeOutput(String codeOutput) {
        this.codeOutput = codeOutput;
        if (codeOutputListener != null) {
            codeOutputListener.accept(codeOutput);
        }
    }

    public void setMcrResults(List<String> mcrResults) {
        this.mcrResults = mcrResults;
        if (mcrResultsListener != null) {
            mcrResultsListener.accept(mcrResults);
        }
    }

    public void setKnowledgeBase(List<String> knowledgeBase) {
        this.knowledgeBase = knowledgeBase;
        if (knowledgeBaseListener != null) {
            knowledgeBaseListener.accept(knowledgeBase);
        }
    }

    public void setCodeOutputListener(Consumer<String> listener) {
        this.codeOutputListener = listener;
    }

    public void setMcrResultsListener(Consumer<List<String>> listener) {
        this.mcrResultsListener = listener;
    }

    public void setKnowledgeBaseListener(Consumer<List<String>> listener) {
        this.knowledgeBaseListener = listener;
    }
}
