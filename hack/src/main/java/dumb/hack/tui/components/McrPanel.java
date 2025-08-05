package dumb.hack.tui.components;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import dumb.hack.HackController;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class McrPanel extends Panel {

    private final HackController controller;
    private final Panel knowledgeBasePanel;
    private final Panel resultsPanel;
    private final ExecutorService executor;

    public McrPanel(HackController controller) {
        super(new LinearLayout(Direction.HORIZONTAL));
        this.controller = controller;
        this.executor = Executors.newSingleThreadExecutor();

        // Left panel for query and results
        Panel leftPanel = new Panel(new LinearLayout(Direction.VERTICAL));
        leftPanel.addComponent(new Label("Enter your query:"));
        TextBox queryBox = new TextBox(new TerminalSize(50, 1));
        leftPanel.addComponent(queryBox);

        resultsPanel = new Panel();
        resultsPanel.setLayoutManager(new LinearLayout(Direction.VERTICAL));
        leftPanel.addComponent(resultsPanel.withBorder(Borders.singleLine("Results")));

        Button submitButton = new Button("Submit", () -> {
            String query = queryBox.getText();
            if (query.isBlank()) {
                return;
            }
            // Run the query on a background thread
            executor.submit(() -> controller.executeMcrQuery(query));
        });
        leftPanel.addComponent(submitButton);
        this.addComponent(leftPanel);

        // Right panel for knowledge base
        knowledgeBasePanel = new Panel(new LinearLayout(Direction.VERTICAL));
        this.addComponent(knowledgeBasePanel.withBorder(Borders.singleLine("Knowledge Base")));

        // Set up listeners to update the UI when the model changes
        controller.getModel().setMcrResultsListener(this::updateResultsPanel);
        controller.getModel().setKnowledgeBaseListener(this::updateKnowledgeBasePanel);
    }

    private void updateResultsPanel(List<String> results) {
        resultsPanel.removeAllComponents();
        for (String result : results) {
            resultsPanel.addComponent(new Label(result));
        }
    }

    private void updateKnowledgeBasePanel(List<String> kb) {
        knowledgeBasePanel.removeAllComponents();
        for (String clause : kb) {
            knowledgeBasePanel.addComponent(new Label(clause));
        }
    }

    public void close() {
        executor.shutdown();
    }
}
