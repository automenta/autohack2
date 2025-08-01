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

        if (code.getBackend() instanceof PijulBackend pijulBackend) {
            try {
                switch (subcommand) {
                    case "new":
                    case "switch":
                        if (args.length < 2) {
                            code.messageHandler.addMessage("system", "Usage: /channel [new|switch] <name>");
                            return;
                        }
                        String name = args[1];
                        pijulBackend.channel(subcommand, name).get();
                        code.messageHandler.addMessage("system", "Switched to channel " + name);
                        break;
                    case "list":
                        String channels = pijulBackend.channelList().get();
                        code.messageHandler.addMessage("system", "Channels:\n" + channels);
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