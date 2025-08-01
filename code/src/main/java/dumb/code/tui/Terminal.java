package dumb.code.tui;

import dumb.code.CommandManager;

public class Terminal {
    private final TerminalPanel terminalPanel;

    public Terminal(CommandManager commandManager, IBreadcrumbManager breadcrumbManager) {
        this.terminalPanel = new TerminalPanel(commandManager, breadcrumbManager);
    }

    public TerminalPanel getTerminalPanel() {
        return terminalPanel;
    }

    public void addMessage(String message) {
        terminalPanel.addMessage(message);
    }
}