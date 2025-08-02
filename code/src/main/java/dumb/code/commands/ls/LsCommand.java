package dumb.code.commands.ls;

import dumb.code.Code;
import dumb.code.MessageHandler;
import dumb.code.commands.Command;

public class LsCommand implements Command {
    private final Code code;
    private final MessageHandler messageHandler;

    public LsCommand(Code code) {
        this.code = code;
        this.messageHandler = code.messageHandler;
    }

    @Override
    public void execute(String[] args) {
        String path = args.length > 0 ? args[0] : ".";
        try {
            java.util.List<String> files = code.fileManager.listFiles(path);
            messageHandler.addMessage("system", String.join("\n", files));
        } catch (java.io.IOException e) {
            messageHandler.addMessage("system", "Error listing files: " + e.getMessage());
        }
    }

}
