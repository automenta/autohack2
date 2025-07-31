package dumb.hack.commands;

import dumb.code.Backend;
import dumb.hack.HackContext;
import dumb.hack.MessageHandler;

import java.util.List;

public class LsCommand implements Command {
    private final HackContext context;
    private final MessageHandler messageHandler;

    public LsCommand(HackContext context) {
        this.context = context;
        this.messageHandler = context.getMessageHandler();
    }

    @Override
    public void init() {
        // Nothing to initialize
    }

    @Override
    public void execute(String[] args) {
        Backend backend = context.getCodebaseManager().getVersioningBackend();
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
