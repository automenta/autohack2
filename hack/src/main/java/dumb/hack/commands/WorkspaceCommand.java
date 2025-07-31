package dumb.hack.commands;

import dumb.hack.MessageHandler;
import dumb.hack.Workspace;

import java.nio.file.Paths;

public class WorkspaceCommand implements Command {

    private final Workspace workspace;
    private final MessageHandler messageHandler;
    //private final MCR mcr;

    public WorkspaceCommand(Workspace workspace, MessageHandler messageHandler) {
        this.workspace = workspace;
        this.messageHandler = messageHandler;
        //this.mcr = mcr;
    }

    @Override
    public void execute(String[] args) {
        if (args.length == 0) {
            messageHandler.handleMessage("Usage: /workspace <add|remove|list> [path]");
            return;
        }

        String subcommand = args[0].toLowerCase();
        switch (subcommand) {
            case "add":
                if (args.length < 2) {
                    messageHandler.handleMessage("Usage: /workspace add <path>");
                } else {
                    addProject(args[1]);
                }
                break;
            case "remove":
                if (args.length < 2) {
                    messageHandler.handleMessage("Usage: /workspace remove <path>");
                } else {
                    workspace.removeProject(Paths.get(args[1]));
                    messageHandler.handleMessage("Removed project: " + args[1]);
                }
                break;
            case "list":
                messageHandler.handleMessage("Projects in workspace:");
                workspace.getProjects().forEach(p -> messageHandler.handleMessage("- " + p.toString()));
                break;
            default:
                messageHandler.handleMessage("Unknown workspace command: " + subcommand);
        }
    }

    private void addProject(String path) {
        //Session session = mcr.createSession(null);
        workspace.addProject(Paths.get(path));
        messageHandler.handleMessage("Added project: " + path);

        // Run parsing in a background thread
        // new Thread(() -> {
        //     messageHandler.handleMessage("Starting to parse project: " + path);
        //     CodebaseParser parser = new CodebaseParser(session);
        //     parser.parseProject(Paths.get(path));
        //     messageHandler.handleMessage("Finished parsing project: " + path);
        // }).start();
    }

    @Override
    public void init() {
        // Nothing to initialize
    }

    @Override
    public void cleanup() {
        // Nothing to clean up
    }
}
