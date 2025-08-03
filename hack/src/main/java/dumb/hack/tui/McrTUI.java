package dumb.hack.tui;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import dumb.mcr.QueryResult;
import dumb.mcr.Session;

public class McrTUI {
    private final Session session;

    public McrTUI(Session session) {
        this.session = session;
    }

    public Panel createPanel() {
        Panel contentPanel = new Panel(new LinearLayout(Direction.VERTICAL));

        contentPanel.addComponent(new Label("Enter your query:"));
        TextBox queryBox = new TextBox(new TerminalSize(50, 1));
        contentPanel.addComponent(queryBox);

        Panel resultsPanel = new Panel();
        resultsPanel.setLayoutManager(new LinearLayout(Direction.VERTICAL));
        contentPanel.addComponent(resultsPanel.withBorder(Borders.singleLine("Results")));

        Button submitButton = new Button("Submit", () -> {
            String query = queryBox.getText();
            if (query.isBlank()) {
                return;
            }
            QueryResult result = session.nquery(query);
            resultsPanel.removeAllComponents();
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
        });
        contentPanel.addComponent(submitButton);

        return contentPanel;
    }
}
