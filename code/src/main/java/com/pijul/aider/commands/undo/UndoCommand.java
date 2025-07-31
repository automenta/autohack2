package com.pijul.aider.commands.undo;

import com.pijul.aider.Container;
import com.pijul.aider.commands.Command;
import com.pijul.aider.MessageHandler;

public class UndoCommand implements Command {
    private final Container container;
    private final MessageHandler messageHandler;

    public UndoCommand(Container container) {
        this.container = container;
        this.messageHandler = container.getMessageHandler();
    }

    @Override
    public void init() {
    }

    @Override
    public void execute(String[] args) {
        messageHandler.addMessage("system", "Undo command is not yet implemented.");
    }

    @Override
    public void cleanup() {
    }
}
