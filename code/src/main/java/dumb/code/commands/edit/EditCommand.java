package dumb.code.commands.edit;

import dumb.code.Backend;
import dumb.code.Context;
import dumb.code.commands.Command;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public record EditCommand(Context context) implements Command {

    @Override
    public void execute(String[] args) {
        if (args.length == 0) {
            context.messageHandler.addMessage("system", "Please specify a file to edit.");
            return;
        }

        String file = args[0];
        try {
            // In a real implementation, this would open an editor
            // For now, we'll just simulate by reading the file
            Path path = Paths.get(file);
            String content = new String(Files.readAllBytes(path));

            // Simulate editing by updating the container
            context.messageHandler.addMessage("system", "Editing " + file);

            // After editing, show diff
            Backend backend = context.getBackend();
            String diff = backend.diff().get();
            context.setDiff(diff);
            context.messageHandler.addMessage("system", "Finished editing " + file);
        } catch (Exception e) {
            context.messageHandler.addMessage("system", "Error: " + e.getMessage());
        }
    }

}