package dumb.hack;

import dumb.hack.commands.Command;

import java.util.HashMap;
import java.util.Map;

public class CommandManager {
    private final Map<String, Command> commands = new HashMap<>();
    private final HackContext context;

    public CommandManager(HackContext context) {
        this.context = context;
    }

    public void add(String name, Command command) {
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
            context.getMessageHandler().handleMessage("Unknown command: " + commandName);
        }
    }
}
