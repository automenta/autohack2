package dumb.code.commands.edit;

import dumb.code.Context;
import dumb.code.commands.Command;
import dumb.code.LMManager;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public record EditCommand(Context context) implements Command {

    @Override
    public void execute(String[] args) {
        if (args.length < 2) {
            context.messageHandler.addMessage("system", "Usage: /edit <file> <prompt>");
            return;
        }

        String file = args[0];
        String prompt = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

        try {
            Path path = Paths.get(file);
            String originalContent = new String(Files.readAllBytes(path));

            LMManager lmManager = context.lmManager;
            String newContent = lmManager.generateResponse("Edit the following file based on the prompt:\n\n" + originalContent + "\n\nPrompt: " + prompt);

            Files.write(path, newContent.getBytes());

            context.messageHandler.addMessage("system", "Finished editing " + file);
        } catch (Exception e) {
            context.messageHandler.addMessage("system", "Error: " + e.getMessage());
        }
    }
}