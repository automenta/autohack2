package dumb.code.commands.help;

import dumb.code.Code;
import dumb.code.MessageHandler;
import dumb.code.commands.Command;
import dumb.code.help.HelpManager;
import dumb.code.help.FirstRunHelp;

import java.util.HashMap;
import java.util.Map;

public class HelpCommand implements Command {
    private final Code code;
    private final HelpManager helpManager;
    private final Map<String, String> commandMap;

    public HelpCommand(Code code, HelpManager helpManager) {
        this.code = code;
        this.helpManager = helpManager;
        this.commandMap = new HashMap<>();
        initializeCommandMap();
    }

    private void initializeCommandMap() {
        commandMap.put("add", "Add a file to the chat and stage it.");
        commandMap.put("clear", "Clear the current codebase context.");
        commandMap.put("codebase", "Show the current codebase.");
        commandMap.put("commit", "Commit the staged changes.");
        commandMap.put("create", "Create a new file.");
        commandMap.put("diff", "Show the current changes.");
        commandMap.put("drop", "Remove a file from the chat.");
        commandMap.put("edit", "Edit a file.");
        commandMap.put("exit", "Exit the interactive terminal.");
        commandMap.put("help", "Show this help message, or use '/help start' or '/help stop' to control the interactive help.");
        commandMap.put("ls", "List files in the current or specified directory.");
        commandMap.put("mv", "Move or rename a file.");
        commandMap.put("grep", "Search for a pattern in files.");
        commandMap.put("apply", "Apply a patch from the chat.");
        commandMap.put("record", "Record the current changes with a message (alias for /commit).");
        commandMap.put("rm", "Remove a file.");
        commandMap.put("run", "Run a shell command or start an interactive terminal.");
        commandMap.put("status", "Show the current status of the repository.");
        commandMap.put("test", "Run the test suite.");
        commandMap.put("undo", "Undo changes to a file or the entire project.");
    }

    @Override
    public void init() {
        // No initialization needed for HelpCommand
    }

    @Override
    public void execute(String[] args) {
        if (args.length > 0) {
            String subCommand = args[0].toLowerCase();
            switch (subCommand) {
                case "start":
                    helpManager.startHelp(new FirstRunHelp());
                    code.messageHandler.onMessage("Interactive help started. 🚀");
                    break;
                case "stop":
                    helpManager.stopHelp();
                    code.messageHandler.onMessage("Interactive help stopped. 👋");
                    break;
                default:
                    code.messageHandler.onMessage("Unknown help command: " + subCommand + ". Use '/help start' or '/help stop'.");
                    break;
            }
            return;
        }

        MessageHandler messageHandler = code.messageHandler;
        StringBuilder helpMessage = new StringBuilder("Available commands:\n");
        for (Map.Entry<String, String> entry : commandMap.entrySet()) {
            helpMessage.append("  /")
                    .append(entry.getKey())
                    .append(" - ")
                    .append(entry.getValue())
                    .append("\n");
        }
        messageHandler.addMessage("system", helpMessage.toString());
    }

    @Override
    public void cleanup() {
        // No cleanup needed for HelpCommand
    }
}