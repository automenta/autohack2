package dumb.hack;

import dumb.code.Backend;
import dumb.code.BackendManager;
import dumb.code.CodebaseManager;
import dumb.hack.commands.*;
import dumb.hack.tools.HackToolProvider;
import dumb.mcr.MCR;
import dumb.mcr.Session;

import java.util.Properties;

public class HackContext {
    private final Workspace workspace;
    private final CommandManager commands;
    private final McpConfig config;
    private final ApiKeyManager apiKeys;
    private final MCR mcr;
    private final Session mcrSession;
    private final BackendManager backendManager;
    private final CodebaseManager codebaseManager;
    private final Backend backend;
    private MessageHandler messageHandler;

    public HackContext() {
        this.workspace = new Workspace();
        this.commands = new CommandManager(this);
        this.config = new McpConfig();
        this.apiKeys = new ApiKeyManager();

        Properties mcrProps = new Properties();
        String provider = System.getProperty("llm.provider", "openai");
        String apiKey = apiKeys.getApiKey(provider);
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

        this.backendManager = new BackendManager(null); // Passing null as container, as it's not used.
        this.backendManager.autodetectBackend();
        this.backend = this.backendManager.getBackend();
        this.codebaseManager = new CodebaseManager(this.backend);
    }

    public void init() {
        commands.add("/workspace", new WorkspaceCommand(workspace, messageHandler));
        commands.add("/mcp", new McpCommand(config, messageHandler));
        commands.add("/query", new QueryCommand(this));
        commands.add("/reason", new ReasonCommand(this));
        commands.add("/apikey", new ApiKeyCommand(this));
        commands.add("/ls", new LsCommand(this));
    }

    public Workspace getWorkspace() {
        return workspace;
    }

    public McpConfig getConfig() {
        return config;
    }

    public CommandManager getCommands() {
        return commands;
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

    public ApiKeyManager getApiKeys() {
        return apiKeys;
    }

    public CodebaseManager getCodebaseManager() {
        return codebaseManager;
    }
}
