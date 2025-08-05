package dumb.hack.tui.components;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import dumb.code.Code;
import dumb.mcr.MCR;
import dumb.mcr.QueryResult;
import dumb.mcr.Session;

public class NewUnifiedPanel extends Panel {

    private final Code code;
    private final MCR mcr;
    private final Session mcrSession;
    private final MultiWindowTextGUI gui;

    // UI Components
    private final Panel conversationPanel;
    private final TextBox inputBox;

    public NewUnifiedPanel(Code code, MCR mcr, MultiWindowTextGUI gui) {
        super(new LinearLayout(Direction.VERTICAL));
        this.code = code;
        this.mcr = mcr;
        this.mcrSession = mcr.createSession();
        this.gui = gui;

        // --- UI Components ---
        this.conversationPanel = new Panel(new LinearLayout(Direction.VERTICAL));
        this.inputBox = new TextBox(new TerminalSize(100, 1), "");

        Panel inputPanel = new Panel(new LinearLayout(Direction.HORIZONTAL));
        inputPanel.addComponent(new Label("Input:"));
        inputPanel.addComponent(inputBox);
        inputPanel.addComponent(new Button("Send", this::handleInput));

        this.addComponent(conversationPanel.withBorder(Borders.singleLine("Conversation")));
        this.addComponent(inputPanel);

        setupCodeMessageHandler();
    }

    private void handleInput() {
        String input = inputBox.getText();
        if (input.isBlank()) {
            return;
        }

        // Add user input to conversation
        conversationPanel.addComponent(new Label("YOU: " + input));
        inputBox.setText("");

        if (input.startsWith("/")) {
            handleCodeCommand(input.substring(1));
        } else {
            handleMcrQuery(input);
        }
    }

    private void handleCodeCommand(String command) {
        code.commandManager.processInput(command);
    }

    private void handleMcrQuery(String query) {
        // Add a thinking indicator
        Label thinkingLabel = new Label("MCR: Thinking...");
        conversationPanel.addComponent(thinkingLabel);

        try {
            QueryResult result = mcrSession.nquery(query);
            conversationPanel.removeComponent(thinkingLabel);
            displayMcrResult(result);
        } catch (Exception e) {
            conversationPanel.removeComponent(thinkingLabel);
            conversationPanel.addComponent(new Label("MCR Error: " + e.getMessage()));
        }
    }

    private void displayMcrResult(QueryResult result) {
        if (!result.success()) {
            conversationPanel.addComponent(new Label("MCR: Query failed."));
            return;
        }

        if (result.bindings() != null && !result.bindings().isEmpty()) {
            conversationPanel.addComponent(new Label("MCR: Found solutions:"));
            result.getBindings().forEach(solution -> {
                Panel solutionPanel = new Panel(new LinearLayout(Direction.HORIZONTAL));
                solutionPanel.addComponent(new Label("  - " + solution.toString()));
                solutionPanel.addComponent(new Button("Run", () -> {
                    handleCodeCommand(solution.toString());
                }));
                conversationPanel.addComponent(solutionPanel);
            });
        } else {
            conversationPanel.addComponent(new Label("MCR: Query was successful, but returned no solutions."));
        }
    }

    private void setupCodeMessageHandler() {
        // The message handler will be called from the command manager thread.
        // We need to update the UI on the UI thread.
        // Lanterna is thread-safe, so this should be fine.
        this.code.getMessageHandler().setListener(message -> {
            conversationPanel.addComponent(new Label("CODE: " + message));
        });
    }


    public void close() {
        // No-op
    }
}
