package dumb.hack.tui.components;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import dumb.mcr.QueryResult;
import dumb.mcr.Result;
import dumb.mcr.Session;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class McrPanel extends Panel {

    /**
     * Listener interface for when a user wants to take action on an MCR result.
     */
    public interface McrResultListener {
        void onResultAction(String resultText);
    }

    private final Session session;
    private final Panel knowledgeBasePanel;
    private final Panel resultsPanel;
    private final ExecutorService executor;
    private McrResultListener listener;

    public McrPanel(Session session) {
        super(new LinearLayout(Direction.VERTICAL));
        this.session = session;
        this.executor = Executors.newSingleThreadExecutor();

        // --- UI Components ---
        // Initialize components first
        TextBox queryBox = new TextBox(new TerminalSize(50, 1));
        this.resultsPanel = new Panel(new LinearLayout(Direction.VERTICAL));
        this.knowledgeBasePanel = new Panel(new LinearLayout(Direction.VERTICAL));

        Button submitButton = new Button("Submit", () -> {
            String query = queryBox.getText();
            if (query.isBlank()) return;
            resultsPanel.removeAllComponents();
            resultsPanel.addComponent(new Label("Thinking...")); // Loading indicator
            executor.submit(() -> executeQuery(query));
        });

        // Add components to the panel in the desired order
        addComponent(new Label("Enter your query:"));
        addComponent(queryBox);
        addComponent(submitButton);
        addComponent(resultsPanel.withBorder(Borders.singleLine("Results")));
        addComponent(knowledgeBasePanel.withBorder(Borders.singleLine("Knowledge Base")));

        updateKnowledgeBasePanel();
    }

    public void setListener(McrResultListener listener) {
        this.listener = listener;
    }

    private void executeQuery(String query) {
        // UI updates are thread-safe in Lanterna
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
            handleNaturalLanguageQuery(query);
        }
    }

    private void handleNaturalLanguageQuery(String query) {
        try {
            QueryResult result = session.nquery(query);
            resultsPanel.addComponent(new Label("Original Query: " + result.originalQuery()));

            if (!result.success()) {
                resultsPanel.addComponent(new Label("MCR query failed."));
                return;
            }

            resultsPanel.addComponent(new Label("Success!"));
            if (result.bindings() != null && !result.bindings().isEmpty()) {
                resultsPanel.addComponent(new Label("Solutions:"));
                result.getBindings().forEach(solution -> {
                    Panel resultLine = new Panel(new LinearLayout(Direction.HORIZONTAL));
                    final String solutionText = "  " + solution;
                    resultLine.addComponent(new Label(solutionText));

                    Button actionButton = new Button("▶ Use", () -> {
                        if (listener != null) {
                            // Notify the listener that the user wants to use this solution
                            listener.onResultAction(solution.toString()); // Send the raw solution
                        }
                    });
                    resultLine.addComponent(actionButton);
                    resultsPanel.addComponent(resultLine);
                });
            } else {
                resultsPanel.addComponent(new Label("Query was successful, but returned no solutions."));
            }
        } catch (Exception e) {
            // Self-diagnosing: display the error in the UI
            resultsPanel.addComponent(new Label("Error during reasoning: " + e.getMessage())
                    .setForegroundColor(TextColor.ANSI.RED));
            // Log the full stack trace for debugging
            e.printStackTrace();
        }
    }

    private void updateKnowledgeBasePanel() {
        knowledgeBasePanel.removeAllComponents();
        for (dumb.prolog.Clause clause : session.getKnowledgeGraph().getClauses()) {
            knowledgeBasePanel.addComponent(new Label(clause.toString()));
        }
    }

    public void close() {
        executor.shutdown();
    }
}
