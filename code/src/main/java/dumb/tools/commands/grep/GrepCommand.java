package dumb.tools.commands.grep;

import dumb.tools.ToolContext;
import dumb.tools.MessageHandler;
import dumb.tools.commands.Command;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class GrepCommand implements Command {
    private final ToolContext toolContext;

    public GrepCommand(ToolContext toolContext) {
        this.toolContext = toolContext;
    }

    @Override
    public void execute(String[] args) {
        MessageHandler messageHandler = toolContext.messageHandler;
        if (args.length < 1) {
            messageHandler.addMessage("system", "Usage: /grep <pattern> [file...]");
            return;
        }

        Pattern pattern = Pattern.compile(args[0]);
        Path startPath = Paths.get(args.length > 1 ? args[1] : "").toAbsolutePath();
        StringBuilder output = new StringBuilder();

        try (Stream<Path> paths = Files.walk(startPath)) {
            paths.filter(Files::isRegularFile)
                 .forEach(path -> {
                     try {
                         String content = new String(Files.readAllBytes(path));
                         Matcher matcher = pattern.matcher(content);
                         if (matcher.find()) {
                             output.append(path).append("\n");
                         }
                     } catch (IOException e) {
                         // Ignore files that can't be read
                     }
                 });
        } catch (IOException e) {
            messageHandler.addMessage("system", "Error: " + e.getMessage());
            return;
        }

        if (!output.isEmpty()) {
            messageHandler.addMessage("system", output.toString());
        } else {
            messageHandler.addMessage("system", "No matches found");
        }
    }
}