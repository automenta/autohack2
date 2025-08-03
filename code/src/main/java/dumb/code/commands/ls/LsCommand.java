package dumb.code.commands.ls;

import dumb.code.MessageHandler;
import dumb.code.commands.Command;
import dumb.code.tools.FileSystemTool;

public class LsCommand implements Command {
    private final FileSystemTool fileSystemTool;
    private final MessageHandler messageHandler;

    public LsCommand(FileSystemTool fileSystemTool, MessageHandler messageHandler) {
        this.fileSystemTool = fileSystemTool;
        this.messageHandler = messageHandler;
    }

    @Override
    public void execute(String[] args) {
        String path = args.length > 0 ? args[0] : ".";
        try {
            java.util.List<String> files = fileSystemTool.listFiles(path);
            messageHandler.addMessage("system", String.join("\n", files));
        } catch (java.io.IOException e) {
            messageHandler.addMessage("system", "Error listing files: " + e.getMessage());
        }
    }

}
