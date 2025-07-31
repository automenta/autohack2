package com.pijul.aider.commands.test;

import com.pijul.aider.Container;
import com.pijul.aider.commands.Command;
import com.pijul.aider.MessageHandler;

public class TestCommand implements Command {
    private final Container container;
    private final MessageHandler messageHandler;

    public TestCommand(Container container) {
        this.container = container;
        this.messageHandler = container.getMessageHandler();
    }

    @Override
    public void init() {
    }

    @Override
    public void execute(String[] args) {
        messageHandler.addMessage("system", "Test command is not yet implemented.");
    }

    @Override
    public void cleanup() {
    }
}
