package dumb.code;

import dumb.code.help.HelpManager;
import dumb.code.tui.Terminal;

public class UIManager {
    private final Code code;
    private final HelpManager helpManager;
    private Terminal terminal;


    public UIManager(Code code) {
        this.code = code;
        this.helpManager = code.getHelpManager();
    }

    private Terminal getTerminal() {
        if (this.terminal == null) {
            this.terminal = this.code.getTerminal();
        }
        return this.terminal;
    }


    public void displayWelcomeMessage() {
        // Display welcome message
        displayMessage("Welcome to AutoHack!  hacking with AI power! 🚀");
        if (helpManager.isHelpActive()) {
            displayHelpMessage();
        }
    }

    public void displayHelpMessage() {
        String helpMessage = helpManager.getCurrentHelpMessage();
        if (helpMessage != null && !helpMessage.isEmpty()) {
            displayMessage("💡 " + helpMessage);
        }
    }

    public void displayMessage(String message) {
        code.messageHandler.onMessage(message);
    }

    public String getUserInput() {
        // This method is problematic in a non-interactive environment.
        // For now, we will return an empty string.
        return "";
    }


    // Add more methods as needed
}