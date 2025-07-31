package com.pijul.aider;

import com.pijul.aider.commands.AddCommand;
import com.pijul.aider.commands.Command;
import com.pijul.aider.commands.ExitCommand;
import com.pijul.aider.commands.HelpCommand;

import java.util.HashMap;
import java.util.Map;

public class CommandManager {
    private final Map<String, Command> commands = new HashMap<>();
    private final PijulAider aider;

    public CommandManager(PijulAider aider) {
        this.aider = aider;
        registerCommands();
    }

    private void registerCommands() {
        registerCommand("/add", new AddCommand(aider));
        registerCommand("/help", new HelpCommand(aider));
        registerCommand("/exit", new ExitCommand(aider));
    }

    public void registerCommand(String name, Command command) {
        commands.put(name, command);
    }

    public void executeCommand(String line) {
        if (line.isEmpty()) return;
        String[] parts = line.split(" ", 2);
        String cmdName = parts[0].toLowerCase();
        String[] args = parts.length > 1 ? parts[1].split(" ") : new String[0];

        Command cmd = commands.get(cmdName);
        if (cmd != null) {
            cmd.execute(args);
        } else {
            // If it's not a command, it's a prompt for the LLM
            String response = aider.getLlmManager().generateResponse(line);
            aider.getUiManager().displayMessage(response);
        }
    }

    public int getCommandCount() {
        return commands.size();
    }
}