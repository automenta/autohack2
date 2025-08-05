package dumb.hack.ui.tui;

import dumb.code.Code;

public class Terminal {
    private final Code code;
    private final TerminalPanel terminalPanel;

    public Terminal(Code code) {
        this.code = code;
        // Pass the submit handler to the panel
        this.terminalPanel = new TerminalPanel(this::handleSubmit);
        setupMessageHandler();
    }

    private void handleSubmit() {
        String input = terminalPanel.getInputText();
        if (input != null && !input.trim().isEmpty()) {
            code.commandManager.processInput(input);
            terminalPanel.clearInput();
        }
    }

    private void setupMessageHandler() {
        this.code.getMessageHandler().setListener(this::displayMessage);
    }

    private void displayMessage(String message) {
        // This should append, not replace. For now, this is fine.
        terminalPanel.setOutputText(message);
    }

    public TerminalPanel getPanel() {
        return terminalPanel;
    }
}
