package dumb.code.commands.channel;

import dumb.code.MessageHandler;
import dumb.code.commands.Command;
import dumb.code.tools.VersionControlTool;

public record ChannelCommand(VersionControlTool versionControlTool, MessageHandler messageHandler) implements Command {

    @Override
    public void execute(String[] args) {
        if (args.length < 1) {
            messageHandler.addMessage("system", "Usage: /channel [new|switch|list] [name]");
            return;
        }

        String subcommand = args[0];
        String name = args.length > 1 ? args[1] : "";

        try {
            switch (subcommand) {
                case "new":
                    versionControlTool.channel("new", name);
                    messageHandler.addMessage("system", "Created channel " + name);
                    break;
                case "switch":
                    versionControlTool.channel("switch", name);
                    messageHandler.addMessage("system", "Switched to channel " + name);
                    break;
                case "list":
                    // This is still a placeholder as the tool abstracts the backend
                    messageHandler.addMessage("system", "Channels: [main]");
                    break;
                default:
                    messageHandler.addMessage("system", "Usage: /channel [new|switch|list] [name]");
            }
        } catch (Exception e) {
            messageHandler.addMessage("system", "Error executing channel command: " + e.getMessage());
        }
    }

}