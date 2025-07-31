package com.pijul.aider;

import com.pijul.aider.tui.Terminal;

public class MessageHandler {
    private final Container container;

    public MessageHandler(Container container) {
        this.container = container;
    }

    public void addMessage(String sender, String message) {
        Terminal terminal = container.getTerminal();
        if (terminal != null) {
            terminal.addMessage(sender + ": " + message);
        } else {
            System.out.println(sender + ": " + message);
        }
    }

    // Add more methods as needed
}