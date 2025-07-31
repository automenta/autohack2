package com.pijul.aider;

public class MessageHandler {
    private PijulAider aider;

    public MessageHandler(PijulAider aider) {
        this.aider = aider;
    }

    public void addMessage(String sender, String message) {
        // Add message logic
        aider.getUiManager().displayMessage(sender + ": " + message);
    }

    // Add more methods as needed
}