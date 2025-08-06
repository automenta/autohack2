package dumb.tools.commands.channel;

import dumb.tools.ToolContext;
import dumb.tools.commands.Command;
import dumb.tools.versioning.PijulBackend;

public record ChannelCommand(ToolContext toolContext) implements Command {

    @Override
    public void execute(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: /channel [new|switch|list] [name]");
            return;
        }

        String subcommand = args[0];
        String name = args.length > 1 ? args[1] : "";

        if (toolContext.getBackend() instanceof PijulBackend pijulBackend) {
            try {
                switch (subcommand) {
                    case "new":
                        pijulBackend.channel("new", name).get();
                        toolContext.messageHandler.addMessage("system", "Created channel " + name);
                        break;
                    case "switch":
                        pijulBackend.channel("switch", name).get();
                        toolContext.messageHandler.addMessage("system", "Switched to channel " + name);
                        break;
                    case "list":
                        // Pijul doesn't have a direct `channel list` command, this is a placeholder
                        toolContext.messageHandler.addMessage("system", "Channels: [main]");
                        break;
                    default:
                        toolContext.messageHandler.addMessage("system", "Usage: /channel [new|switch|list] [name]");
                }
            } catch (Exception e) {
                toolContext.messageHandler.addMessage("system", "Error executing channel command: " + e.getMessage());
            }
        } else {
            toolContext.messageHandler.addMessage("system", "This backend does not support channels.");
        }
    }

}