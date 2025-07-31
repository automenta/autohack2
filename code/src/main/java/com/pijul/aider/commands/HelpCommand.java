package com.pijul.aider.commands;

import com.pijul.aider.PijulAider;

public class HelpCommand implements Command {
    private final PijulAider aider;

    public HelpCommand(PijulAider aider) {
        this.aider = aider;
    }

    @Override
    public void execute(String[] args) {
        aider.getUiManager().displayMessage("Available commands:\n" +
                "/add <file> - Add a file to the chat\n" +
                "/diff - Show the current changes\n" +
                "/record <message> - Record the current changes with a message\n" +
                "/help - Show this help message\n" +
                "/exit - Exit the application");
    }
}
