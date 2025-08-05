package dumb.hack.tui.components;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import dumb.mcr.QueryResult;
import dumb.mcr.Result;
import dumb.mcr.Session;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class McrPanel extends Panel {

    private final Session session;
    private final Panel knowledgeBasePanel;
    private final Panel resultsPanel;
    private final ExecutorService executor;

    public McrPanel(Session session) {
        super(new LinearLayout(Direction.HORIZONTAL));
        this.session = session;
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
            executor.submit(() -> {
                executeQuery(query);
            });
        });
        leftPanel.addComponent(submitButton);
        this.addComponent(leftPanel);

        // Right panel for knowledge base
        knowledgeBasePanel = new Panel(new LinearLayout(Direction.VERTICAL));
        this.addComponent(knowledgeBasePanel.withBorder(Borders.singleLine("Knowledge Base")));

        updateKnowledgeBasePanel();
    }

    private void executeQuery(String query) {
        // Clear previous results
        // Note: this needs to be done on the UI thread.
        // Lanterna is thread-safe, so we can call it directly.
        resultsPanel.removeAllComponents();

        String trimmedQuery = query.trim();

        if (trimmedQuery.startsWith("assertProlog(")) {
            String clause = trimmedQuery.substring("assertProlog(".length(), trimmedQuery.length() - 1);
            Result result = session.assertProlog(clause);
            resultsPanel.addComponent(new Label(result.message()));
            updateKnowledgeBasePanel();
        } else if (trimmedQuery.startsWith("retractProlog(")) {
            String clause = trimmedQuery.substring("retractProlog(".length(), trimmedQuery.length() - 1);
            Result result = session.retractProlog(clause);
            resultsPanel.addComponent(new Label(result.message()));
            updateKnowledgeBasePanel();
        } else {
            QueryResult result = session.nquery(query);
            resultsPanel.addComponent(new Label("Original Query: " + result.originalQuery()));
            if (result.success()) {
                resultsPanel.addComponent(new Label("Success!"));
                if (result.bindings() != null && !result.bindings().isEmpty()) {
                    resultsPanel.addComponent(new Label("Solutions:"));
                    result.getBindings().forEach(solution -> resultsPanel.addComponent(new Label("  " + solution)));
                } else {
                    resultsPanel.addComponent(new Label("Query was successful, but returned no solutions."));
                }
            } else {
                resultsPanel.addComponent(new Label("MCR query failed."));
            }
        }
    }


    private void updateKnowledgeBasePanel() {
        // This needs to be done on the UI thread.
        // Lanterna is thread-safe.
        knowledgeBasePanel.removeAllComponents();
        for (dumb.prolog.Clause clause : session.getKnowledgeGraph().getClauses()) {
            knowledgeBasePanel.addComponent(new Label(clause.toString()));
        }
    }

    public void close() {
        executor.shutdown();
    }
}
