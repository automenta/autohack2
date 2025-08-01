package dumb.code.commands.run;

import dumb.code.Context;
import dumb.code.MessageHandler;
import dumb.code.commands.Command;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class RunCommand implements Command {
    private final Context context;

    public RunCommand(Context context) {
        this.context = context;
    }

    @Override
    public void execute(String[] args) {
        MessageHandler messageHandler = context.getMessageHandler();
        if (args.length == 0) {
            messageHandler.addMessage("system", "Usage: /run <command>");
            return;
        }

        try {
            ProcessBuilder processBuilder = new ProcessBuilder(args);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }

            int exitCode = process.waitFor();
            messageHandler.addMessage("system", "Command finished with exit code " + exitCode + ":\n" + output);

        } catch (IOException | InterruptedException e) {
            messageHandler.addMessage("system", "Error executing command: " + e.getMessage());
        }
    }

}
