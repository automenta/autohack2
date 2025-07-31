package dumb.code.commands.grep;

import dumb.code.Context;
import dumb.code.MessageHandler;
import dumb.code.commands.Command;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GrepCommand implements Command {
    private final Context context;

    public GrepCommand(Context context) {
        this.context = context;
    }

    @Override
    public void execute(String[] args) {
        MessageHandler messageHandler = context.messageHandler;
        if (args.length < 1) {
            messageHandler.addMessage("system", "Usage: /grep <pattern> [files...]");
            return;
        }

        String pattern = args[0];
        List<String> files = new ArrayList<>(Arrays.asList(args).subList(1, args.length));

        try {
            List<String> command = new ArrayList<>();
            command.add("grep");
            command.add("-r");
            command.add(pattern);
            command.addAll(files);

            ProcessBuilder pb = new ProcessBuilder(command);
            pb.directory(new File(System.getProperty("user.dir")));
            Process process = pb.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            int exitCode = process.waitFor();
            if (exitCode == 0) {
                messageHandler.addMessage("system", output.toString());
            } else {
                messageHandler.addMessage("system", "No matches found");
            }
        } catch (IOException | InterruptedException e) {
            messageHandler.addMessage("system", "Error: " + e.getMessage());
        }
    }

}