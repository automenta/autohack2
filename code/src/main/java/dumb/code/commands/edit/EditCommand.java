package dumb.code.commands.edit;

import dumb.code.LMManager;
import dumb.code.MessageHandler;
import dumb.code.commands.Command;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public record EditCommand(LMManager lmManager, MessageHandler messageHandler) implements Command {

    @Override
    public void execute(String[] args) {
        if (args.length < 2) {
            messageHandler.addMessage("system", "Usage: /edit <file> <prompt>");
            return;
        }

        String file = args[0];
        String prompt = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

        try {
            Path path = Paths.get(file);
            String originalContent = new String(Files.readAllBytes(path));

            String newContent = lmManager.generateResponse("Edit the following file based on the prompt:\n\n" + originalContent + "\n\nPrompt: " + prompt);

            Files.write(path, newContent.getBytes());

            messageHandler.addMessage("system", "Finished editing " + file);
        } catch (Exception e) {
            messageHandler.addMessage("system", "Error: " + e.getMessage());
        }
    }
}