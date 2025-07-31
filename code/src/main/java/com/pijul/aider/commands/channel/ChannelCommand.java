package com.pijul.aider.commands.channel;

import com.pijul.aider.Container;
import com.pijul.aider.commands.Command;

public class ChannelCommand implements Command {

    private final Container container;

    public ChannelCommand(Container container) {
        this.container = container;
    }

    @Override
    public void init() {
    }

    @Override
    public void execute(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: /channel [new|switch|list] [name]");
            return;
        }

        String subcommand = args[0];
        String name = args.length > 1 ? args[1] : "";

        if (container.getBackend() instanceof com.pijul.aider.PijulBackend) {
            com.pijul.aider.PijulBackend pijulBackend = (com.pijul.aider.PijulBackend) container.getBackend();
            try {
                switch (subcommand) {
                    case "new":
                        pijulBackend.channel("new", name).get();
                        container.getMessageHandler().addMessage("system", "Created channel " + name);
                        break;
                    case "switch":
                        pijulBackend.channel("switch", name).get();
                        container.getMessageHandler().addMessage("system", "Switched to channel " + name);
                        break;
                    case "list":
                        // Pijul doesn't have a direct `channel list` command, this is a placeholder
                        container.getMessageHandler().addMessage("system", "Channels: [main]");
                        break;
                    default:
                        container.getMessageHandler().addMessage("system", "Usage: /channel [new|switch|list] [name]");
                }
            } catch (Exception e) {
                container.getMessageHandler().addMessage("system", "Error executing channel command: " + e.getMessage());
            }
        } else {
            container.getMessageHandler().addMessage("system", "This backend does not support channels.");
        }
    }

    @Override
    public void cleanup() {
    }
}