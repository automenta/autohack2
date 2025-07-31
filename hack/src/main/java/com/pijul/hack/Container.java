package com.pijul.hack;

import com.example.mcr.core.MCR;
import com.pijul.aider.llm.LLMChain;
import com.pijul.hack.commands.McpCommand;
import com.pijul.hack.commands.QueryCommand;
import com.pijul.hack.commands.ReasonCommand;
import com.pijul.hack.commands.WorkspaceCommand;
import dev.langchain4j.mcp.McpToolProvider;

import java.util.ArrayList;
import java.util.List;

public class Container {
    private final Workspace workspace;
    private final CommandManager commandManager;
    private final McpConfig mcpConfig;
    private final MCR mcr;
    private McpToolProvider mcpToolProvider;
    private MessageHandler messageHandler;
    private LLMChain llmChain;

    public Container() {
        this.workspace = new Workspace();
        this.commandManager = new CommandManager(this);
        this.mcpConfig = new McpConfig();
        MCR.LlmConfig llmConfig = new MCR.LlmConfig();
        llmConfig.provider = "openai"; // or any other provider
        llmConfig.apiKey = System.getenv("OPENAI_API_KEY");
        llmConfig.model = "gpt-4";
        MCR.Config mcrConfig = new MCR.Config();
        mcrConfig.llm = llmConfig;
        this.mcr = new MCR(mcrConfig);
        // The message handler will be set by the TUI
    }

    public void init() {
        commandManager.registerCommand("/workspace", new WorkspaceCommand(workspace, messageHandler));
        commandManager.registerCommand("/mcp", new McpCommand(mcpConfig, messageHandler));
        commandManager.registerCommand("/query", new QueryCommand(this));
        commandManager.registerCommand("/reason", new ReasonCommand(this));
        List<Object> toolProviders = new ArrayList<>();
        toolProviders.add(getMcpToolProvider());
        this.llmChain = new LLMChain("ollama", "llama2", "", new com.pijul.aider.Container(new String[0]), toolProviders);
    }

    public LLMChain getLlmChain() {
        return llmChain;
    }

    public Workspace getWorkspace() {
        return workspace;
    }

    public McpConfig getMcpConfig() {
        return mcpConfig;
    }

    public McpToolProvider getMcpToolProvider() {
        if (this.mcpToolProvider == null) {
            this.mcpToolProvider = McpProviderFactory.create(mcpConfig);
        }
        return this.mcpToolProvider;
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
}
