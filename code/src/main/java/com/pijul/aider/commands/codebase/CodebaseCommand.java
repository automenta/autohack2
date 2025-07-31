package com.pijul.aider.commands.codebase;

import com.pijul.aider.Container;
import com.pijul.aider.commands.Command;
import com.pijul.aider.MessageHandler;

public class CodebaseCommand implements Command {
    private final Container container;
    private final MessageHandler messageHandler;

    public CodebaseCommand(Container container) {
        this.container = container;
        this.messageHandler = container.getMessageHandler();
    }

    @Override
    public void init() {
    }

    @Override
    public void execute(String[] args) {
        messageHandler.addMessage("system", "Codebase command is not yet implemented.");
    }

    @Override
    public void cleanup() {
    }
}
