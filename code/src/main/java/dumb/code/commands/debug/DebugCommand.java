package dumb.code.commands.debug;

import dumb.code.MessageHandler;
import dumb.code.commands.Command;
import dumb.lm.LMManager;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public record DebugCommand(LMManager lmManager, MessageHandler messageHandler) implements Command {

    @Override
    public void execute(String[] args) {
        if (args.length < 2) {
            messageHandler.addMessage("system", "Usage: /debug <file> <description of bug>");
            return;
        }

        String file = args[0];
        String prompt = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

        try {
            Path path = Paths.get(file);
            String originalContent = new String(Files.readAllBytes(path));

            String newContent = lmManager.generateResponse("Fix the bug in the following file based on the description:\n\n" + originalContent + "\n\nBug description: " + prompt);

            Files.write(path, newContent.getBytes());

            messageHandler.addMessage("system", "Finished debugging " + file);
        } catch (Exception e) {
            messageHandler.addMessage("system", "Error: " + e.getMessage());
        }
    }
}
