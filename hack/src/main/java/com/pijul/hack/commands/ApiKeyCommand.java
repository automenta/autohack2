package com.pijul.hack.commands;

import com.pijul.hack.ApiKeyManager;
import com.pijul.hack.Container;

public class ApiKeyCommand implements Command {

    private final Container container;

    public ApiKeyCommand(Container container) {
        this.container = container;
    }

    @Override
    public void execute(String[] args) {
        if (args.length != 2) {
            container.getMessageHandler().handleMessage("Usage: /apikey <provider> <api_key>");
            return;
        }

        String provider = args[0];
        String apiKey = args[1];

        ApiKeyManager apiKeyManager = container.getApiKeyManager();
        apiKeyManager.setApiKey(provider, apiKey);

        container.getMessageHandler().handleMessage("API key for " + provider + " has been set.");
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
