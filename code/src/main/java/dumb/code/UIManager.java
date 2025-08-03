package dumb.code;

import dumb.code.tui.Terminal;

public class UIManager {
    private final MessageHandler messageHandler;
    private Terminal terminal;


    public UIManager(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    public void setTerminal(Terminal terminal) {
        this.terminal = terminal;
        this.messageHandler.setTerminal(terminal);
    }

    private Terminal getTerminal() {
        return this.terminal;
    }


    public void displayWelcomeMessage() {
        // Display welcome message
        messageHandler.onMessage("Welcome to PijulAider!");
    }

    public String getUserInput() {
        // This method is problematic in a non-interactive environment.
        // For now, we will return an empty string.
        return "";
    }


    // Add more methods as needed
}