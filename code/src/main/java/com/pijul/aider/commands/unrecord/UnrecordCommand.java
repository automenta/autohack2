package com.pijul.aider.commands.unrecord;

import com.pijul.aider.Container;
import com.pijul.aider.MessageHandler;
import com.pijul.aider.commands.Command;

public class UnrecordCommand implements Command {
    private final Container container;
    private final MessageHandler messageHandler;

    public UnrecordCommand(Container container) {
        this.container = container;
        this.messageHandler = container.getMessageHandler();
    }

    @Override
    public void init() {
    }

    @Override
    public void execute(String[] args) {
        messageHandler.addMessage("system", "Unrecord command is not yet implemented.");
    }

    @Override
    public void cleanup() {
    }
}
