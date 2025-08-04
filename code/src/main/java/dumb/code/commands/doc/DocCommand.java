package dumb.code.commands.doc;

import dumb.code.MessageHandler;
import dumb.code.commands.Command;
import dumb.lm.LMManager;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public record DocCommand(LMManager lmManager, MessageHandler messageHandler) implements Command {

    @Override
    public void execute(String[] args) {
        if (args.length != 1) {
            messageHandler.addMessage("system", "Usage: /doc <file>");
            return;
        }

        String file = args[0];

        try {
            Path path = Paths.get(file);
            String originalContent = new String(Files.readAllBytes(path));

            String newContent = lmManager.generateResponse("Add documentation to the following file:\n\n" + originalContent);

            Files.write(path, newContent.getBytes());

            messageHandler.addMessage("system", "Finished documenting " + file);
        } catch (Exception e) {
            messageHandler.addMessage("system", "Error: " + e.getMessage());
        }
    }
}
