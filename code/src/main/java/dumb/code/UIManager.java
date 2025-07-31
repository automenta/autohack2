package dumb.code;

import dumb.code.tui.Terminal;

import java.io.IOException;

public class UIManager {
    private final Context context;
    private Terminal terminal;


    public UIManager(Context context) {
        this.context = context;
    }

    private Terminal getTerminal() {
        if (this.terminal == null) {
            this.terminal = this.context.getTerminal();
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