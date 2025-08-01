package dumb.code;

import dumb.code.tui.Terminal;

import java.io.IOException;

public class UIManager {
    private final Code code;
    private Terminal terminal;


    public UIManager(Code code) {
        this.code = code;
    }

    private Terminal getTerminal() {
        if (this.terminal == null) {
            this.terminal = this.code.getTerminal();
        }
        return this.terminal;
    }


    public void displayWelcomeMessage() {
        // Display welcome message
        code.messageHandler.onMessage("Welcome to PijulAider!");
    }

    public String getUserInput() {
        // This method is problematic in a non-interactive environment.
        // For now, we will return an empty string.
        return "";
    }


    // Add more methods as needed
}