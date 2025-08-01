package dumb.code.tui;

import dumb.code.CommandManager;

public class Terminal {
    private final TerminalPanel terminalPanel;

    public Terminal(CommandManager commandManager) {
        this.terminalPanel = new TerminalPanel(commandManager);
    }

    public TerminalPanel getTerminalPanel() {
        return terminalPanel;
    }

    public void addMessage(String message) {
        terminalPanel.addMessage(message);
    }
}