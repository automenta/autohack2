package com.pijul.hack;

import com.pijul.hack.tools.HackToolProvider;
import com.pijul.mcr.MCR;
import com.pijul.mcr.Session;
import com.pijul.hack.commands.ApiKeyCommand;
import com.pijul.hack.commands.McpCommand;
import com.pijul.hack.commands.QueryCommand;
import com.pijul.hack.commands.ReasonCommand;
import com.pijul.hack.commands.WorkspaceCommand;

import java.util.Properties;

public class Container {
    private final Workspace workspace;
    private final CommandManager commandManager;
    private final McpConfig mcpConfig;
    private final ApiKeyManager apiKeyManager;
    private final MCR mcr;
    private final Session mcrSession;
    private MessageHandler messageHandler;
    private final com.pijul.aider.BackendManager backendManager;
    private final com.pijul.aider.CodebaseManager codebaseManager;
    private final com.pijul.aider.Backend backend;


    public Container() {
        this.workspace = new Workspace();
        this.commandManager = new CommandManager(this);
        this.mcpConfig = new McpConfig();
        this.apiKeyManager = new ApiKeyManager();

        Properties mcrProps = new Properties();
        String provider = System.getProperty("llm.provider", "openai");
        String apiKey = apiKeyManager.getApiKey(provider);
        if (apiKey == null) {
            apiKey = System.getenv(provider.toUpperCase() + "_API_KEY");
        }

        if (apiKey == null || apiKey.isEmpty()) {
            mcrProps.setProperty("llm.provider", "mock");
        } else {
            mcrProps.setProperty("llm.provider", provider);
            mcrProps.setProperty("llm.apiKey", apiKey);
        }
        mcrProps.setProperty("llm.model", "gpt-4o-mini");
        this.mcr = new MCR(mcrProps);
        this.mcrSession = mcr.createSession(new HackToolProvider());

        this.backendManager = new com.pijul.aider.BackendManager(null); // Passing null as container, as it's not used.
        this.backendManager.autodetectBackend();
        this.backend = this.backendManager.getBackend();
        this.codebaseManager = new com.pijul.aider.CodebaseManager(this.backend);
    }

    public void init() {
        commandManager.registerCommand("/workspace", new WorkspaceCommand(workspace, messageHandler));
        commandManager.registerCommand("/mcp", new McpCommand(mcpConfig, messageHandler));
        commandManager.registerCommand("/query", new QueryCommand(this));
        commandManager.registerCommand("/reason", new ReasonCommand(this));
        commandManager.registerCommand("/apikey", new ApiKeyCommand(this));
        commandManager.registerCommand("/ls", new com.pijul.hack.commands.LsCommand(this));
    }

    public Workspace getWorkspace() {
        return workspace;
    }

    public McpConfig getMcpConfig() {
        return mcpConfig;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public MessageHandler getMessageHandler() {
        return messageHandler;
    }

    public void setMessageHandler(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    public MCR getMcr() {
        return mcr;
    }

    public Session getMcrSession() {
        return mcrSession;
    }

    public ApiKeyManager getApiKeyManager() {
        return apiKeyManager;
    }

    public com.pijul.aider.CodebaseManager getCodebaseManager() {
        return codebaseManager;
    }
}
