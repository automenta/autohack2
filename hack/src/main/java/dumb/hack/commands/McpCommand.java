package dumb.hack.commands;

import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import dumb.hack.McpConfig;
import dumb.hack.MessageHandler;

import java.util.Arrays;

public record McpCommand(McpConfig mcpConfig, MessageHandler messageHandler) implements Command {

    @Override
    public void execute(String[] args) {
        if (args.length == 0) {
            messageHandler.handleMessage("Usage: /mcp <set-json|status>");
            messageHandler.handleMessage("  /mcp set-json <json_config>");
            messageHandler.handleMessage("  /mcp status");
            return;
        }

        String subcommand = args[0].toLowerCase();
        switch (subcommand) {
            case "set-json":
                if (args.length < 2) {
                    messageHandler.handleMessage("Usage: /mcp set-json <json_config>");
                    return;
                }
                handleSetJson(Arrays.copyOfRange(args, 1, args.length));
                break;
            case "status":
                messageHandler.handleMessage("Current MCP Configuration:");
                messageHandler.handleMessage(mcpConfig.toString());
                break;
            default:
                messageHandler.handleMessage("Unknown mcp command: " + subcommand);
        }
    }

    private void handleSetJson(String[] jsonParts) {
        String jsonString = String.join(" ", jsonParts);
        try {
            JsonParser.parseString(jsonString); // Validate JSON
            mcpConfig.setJsonConfig(jsonString);
            messageHandler.handleMessage("MCP configuration updated.");
            messageHandler.handleMessage(mcpConfig.toString());
        } catch (JsonSyntaxException e) {
            messageHandler.handleMessage("Invalid JSON provided: " + e.getMessage());
        }
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
