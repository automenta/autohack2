package com.pijul.aider.commands.drop;

import com.pijul.aider.CodebaseManager;
import com.pijul.aider.Container;
import com.pijul.aider.MessageHandler;
import com.pijul.aider.commands.Command;

public class DropCommand implements Command {
    private final Container container;

    public DropCommand(Container container) {
        this.container = container;
    }

    @Override
    public void init() {
        // No initialization needed for DropCommand
    }

    @Override
    public void execute(String[] args) {
        CodebaseManager codebaseManager = container.getCodebaseManager();
        MessageHandler messageHandler = container.getMessageHandler();

        for (String file : args) {
            if (codebaseManager.getFiles().contains(file)) {
                codebaseManager.removeFile(file);
                messageHandler.addMessage("system", "Removed " + file + " from the chat.");
            } else {
                messageHandler.addMessage("system", "File " + file + " not found in the chat.");
            }
        }
    }

    @Override
    public void cleanup() {
        // No cleanup needed for DropCommand
    }
}