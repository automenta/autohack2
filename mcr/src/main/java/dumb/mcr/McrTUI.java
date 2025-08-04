package dumb.mcr;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;

public class McrTUI {
    private final Session session;
    private Panel knowledgeBasePanel;


    public McrTUI(Session session) {
        this.session = session;
    }

    public Panel createPanel() {
        Panel mainPanel = new Panel(new LinearLayout(Direction.HORIZONTAL));

        // Left panel for query and results
        Panel leftPanel = new Panel(new LinearLayout(Direction.VERTICAL));
        leftPanel.addComponent(new Label("Enter your query:"));
        TextBox queryBox = new TextBox(new TerminalSize(50, 1));
        leftPanel.addComponent(queryBox);

        Panel resultsPanel = new Panel();
        resultsPanel.setLayoutManager(new LinearLayout(Direction.VERTICAL));
        leftPanel.addComponent(resultsPanel.withBorder(Borders.singleLine("Results")));

        Button submitButton = new Button("Submit", () -> {
            String query = queryBox.getText();
            if (query.isBlank()) {
                return;
            }

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
        });
        leftPanel.addComponent(submitButton);
        mainPanel.addComponent(leftPanel);

        // Right panel for knowledge base
        knowledgeBasePanel = new Panel(new LinearLayout(Direction.VERTICAL));
        mainPanel.addComponent(knowledgeBasePanel.withBorder(Borders.singleLine("Knowledge Base")));

        updateKnowledgeBasePanel();

        return mainPanel;
    }

    private void updateKnowledgeBasePanel() {
        knowledgeBasePanel.removeAllComponents();
        for (dumb.prolog.Clause clause : session.getKnowledgeGraph().getClauses()) {
            knowledgeBasePanel.addComponent(new Label(clause.toString()));
        }
    }
}
