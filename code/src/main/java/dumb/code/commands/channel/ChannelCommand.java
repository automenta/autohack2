package dumb.code.commands.channel;

import dumb.code.Code;
import dumb.code.commands.Command;
import dumb.code.versioning.PijulBackend;

public record ChannelCommand(Code code) implements Command {

    @Override
    public void execute(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: /channel [new|switch|list] [name]");
            return;
        }

        String subcommand = args[0];
        String name = args.length > 1 ? args[1] : "";

        if (code.getBackend() instanceof PijulBackend pijulBackend) {
            try {
                switch (subcommand) {
                    case "new":
                        pijulBackend.channel("new", name).get();
                        code.messageHandler.addMessage("system", "Created channel " + name);
                        break;
                    case "switch":
                        pijulBackend.channel("switch", name).get();
                        code.messageHandler.addMessage("system", "Switched to channel " + name);
                        break;
                    case "list":
                        // Pijul doesn't have a direct `channel list` command, this is a placeholder
                        code.messageHandler.addMessage("system", "Channels: [main]");
                        break;
                    default:
                        code.messageHandler.addMessage("system", "Usage: /channel [new|switch|list] [name]");
                }
            } catch (Exception e) {
                code.messageHandler.addMessage("system", "Error executing channel command: " + e.getMessage());
            }
        } else {
            code.messageHandler.addMessage("system", "This backend does not support channels.");
        }
    }

}