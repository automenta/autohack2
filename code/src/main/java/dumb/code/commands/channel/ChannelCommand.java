package dumb.code.commands.channel;

import dumb.code.Context;
import dumb.code.PijulBackend;
import dumb.code.commands.Command;

public record ChannelCommand(Context context) implements Command {

    @Override
    public void execute(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: /channel [new|switch|list] [name]");
            return;
        }

        String subcommand = args[0];
        String name = args.length > 1 ? args[1] : "";

        if (context.getBackend() instanceof PijulBackend pijulBackend) {
            try {
                switch (subcommand) {
                    case "new":
                        pijulBackend.channel("new", name).get();
                        context.messageHandler.addMessage("system", "Created channel " + name);
                        break;
                    case "switch":
                        pijulBackend.channel("switch", name).get();
                        context.messageHandler.addMessage("system", "Switched to channel " + name);
                        break;
                    case "list":
                        // Pijul doesn't have a direct `channel list` command, this is a placeholder
                        context.messageHandler.addMessage("system", "Channels: [main]");
                        break;
                    default:
                        context.messageHandler.addMessage("system", "Usage: /channel [new|switch|list] [name]");
                }
            } catch (Exception e) {
                context.messageHandler.addMessage("system", "Error executing channel command: " + e.getMessage());
            }
        } else {
            context.messageHandler.addMessage("system", "This backend does not support channels.");
        }
    }

}