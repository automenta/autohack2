package com.pijul.hack.commands;

import com.pijul.hack.Container;
import com.pijul.hack.MessageHandler;
import com.pijul.aider.CodebaseManager;

import java.util.List;

public class LsCommand implements Command {
    private final Container container;
    private final MessageHandler messageHandler;

    public LsCommand(Container container) {
        this.container = container;
        this.messageHandler = container.getMessageHandler();
    }

    @Override
    public void init() {
        // Nothing to initialize
    }

    @Override
    public void execute(String[] args) {
        com.pijul.aider.Backend backend = container.getCodebaseManager().getVersioningBackend();
        try {
            List<String> files = backend.listTrackedFiles().get();
            StringBuilder fileList = new StringBuilder("Tracked files:\n");
            for (String file : files) {
                fileList.append(file).append("\n");
            }
            messageHandler.handleMessage(fileList.toString());
        } catch (Exception e) {
            messageHandler.handleMessage("Error listing files: " + e.getMessage());
        }
    }

    @Override
    public void cleanup() {
        // Nothing to cleanup
    }
}
