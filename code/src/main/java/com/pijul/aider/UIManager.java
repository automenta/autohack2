package com.pijul.aider;

import com.pijul.aider.tui.Terminal;

import java.io.IOException;

public class UIManager {
    private final Container container;
    private Terminal terminal;


    public UIManager(Container container) {
        this.container = container;
    }

    private Terminal getTerminal() {
        if (this.terminal == null) {
            this.terminal = this.container.getTerminal();
        }
        return this.terminal;
    }


    public void displayWelcomeMessage() {
        // Display welcome message
        displayMessage("Welcome to PijulAider!");
    }

    public void displayMessage(String message) {
        try {
            getTerminal().getScreen().clear();
            getTerminal().getScreen().newTextGraphics().putString(0, 0, message);
            getTerminal().getScreen().refresh();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getUserInput() {
        // This method is problematic in a non-interactive environment.
        // For now, we will return an empty string.
        return "";
    }


    // Add more methods as needed
}