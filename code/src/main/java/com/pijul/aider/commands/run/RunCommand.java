package com.pijul.aider.commands.run;

import com.pijul.aider.Container;
import com.pijul.aider.commands.Command;
import com.pijul.aider.MessageHandler;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

public class RunCommand implements Command {
    private final Container container;
    private final MessageHandler messageHandler;

    public RunCommand(Container container) {
        this.container = container;
        this.messageHandler = container.getMessageHandler();
    }

    @Override
    public void init() {
    }

    @Override
    public void execute(String[] args) {
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
            messageHandler.addMessage("system", "Command finished with exit code " + exitCode + ":\n" + output.toString());

        } catch (IOException | InterruptedException e) {
            messageHandler.addMessage("system", "Error executing command: " + e.getMessage());
        }
    }

    @Override
    public void cleanup() {
    }
}
