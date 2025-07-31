package dumb.hack.commands;

import dumb.hack.ApiKeyManager;
import dumb.hack.HackContext;

public record ApiKeyCommand(HackContext context) implements Command {

    @Override
    public void execute(String[] args) {
        if (args.length != 2) {
            context.getMessageHandler().handleMessage("Usage: /apikey <provider> <api_key>");
            return;
        }

        String provider = args[0];
        String apiKey = args[1];

        ApiKeyManager apiKeyManager = context.getApiKeys();
        apiKeyManager.setApiKey(provider, apiKey);

        context.getMessageHandler().handleMessage("API key for " + provider + " has been set.");
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
