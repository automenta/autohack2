package com.pijul.hack;

import com.pijul.hack.commands.Command;

import java.util.HashMap;
import java.util.Map;

public class CommandManager {
    private final Map<String, Command> commands = new HashMap<>();
    private final Container container;

    public CommandManager(Container container) {
        this.container = container;
    }

    public void registerCommand(String name, Command command) {
        commands.put(name, command);
    }

    public void processInput(String input) {
        if (input == null || input.trim().isEmpty() || !input.startsWith("/")) {
            // Not a command, handle as a regular message or ignore
            return;
        }

        String[] parts = input.trim().substring(1).split(" ", 2);
        String commandName = "/" + parts[0].toLowerCase();
        String[] args = parts.length > 1 ? parts[1].split(" ") : new String[0];

        Command command = commands.get(commandName);
        if (command != null) {
            command.execute(args);
        } else {
            container.getMessageHandler().handleMessage("Unknown command: " + commandName);
        }
    }
}
