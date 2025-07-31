package com.pijul.hack;

import com.pijul.hack.tools.HackToolProvider;
import com.pijul.mcr.MCR;
import com.pijul.mcr.Session;
import com.pijul.hack.commands.McpCommand;
import com.pijul.hack.commands.QueryCommand;
import com.pijul.hack.commands.ReasonCommand;
import com.pijul.hack.commands.WorkspaceCommand;

import java.util.Properties;

public class Container {
    private final Workspace workspace;
    private final CommandManager commandManager;
    private final McpConfig mcpConfig;
    private final MCR mcr;
    private final Session mcrSession;
    private MessageHandler messageHandler;

    public Container() {
        this.workspace = new Workspace();
        this.commandManager = new CommandManager(this);
        this.mcpConfig = new McpConfig();

        Properties mcrProps = new Properties();
        mcrProps.setProperty("llm.provider", "openai");
        mcrProps.setProperty("llm.apiKey", System.getenv("OPENAI_API_KEY"));
        mcrProps.setProperty("llm.model", "gpt-4o-mini");
        this.mcr = new MCR(mcrProps);
        this.mcrSession = mcr.createSession(new HackToolProvider());
    }

    public void init() {
        commandManager.registerCommand("/workspace", new WorkspaceCommand(workspace, messageHandler));
        commandManager.registerCommand("/mcp", new McpCommand(mcpConfig, messageHandler));
        commandManager.registerCommand("/query", new QueryCommand(this));
        commandManager.registerCommand("/reason", new ReasonCommand(this));
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
}
