package com.pijul.hack;

import com.pijul.hack.commands.WorkspaceCommand;
//import com.example.mcr.core.MCR;

public class Container {
    private final Workspace workspace;
    private final CommandManager commandManager;
    //private final MCR mcr;
    private MessageHandler messageHandler;

    public Container() {
        this.workspace = new Workspace();
        this.commandManager = new CommandManager(this);
        //this.mcr = new MCR(null); // Passing null for config, assuming it's handled internally
        // The message handler will be set by the TUI
    }

    public void init() {
        commandManager.registerCommand("/workspace", new WorkspaceCommand(workspace, messageHandler));
        //commandManager.registerCommand("/query", new com.pijul.hack.commands.QueryCommand(workspace, messageHandler));
        //commandManager.registerCommand("/reason", new com.pijul.hack.commands.ReasonCommand(workspace, messageHandler));
    }

    public Workspace getWorkspace() {
        return workspace;
    }

    // public MCR getMcr() {
    //     return mcr;
    // }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public MessageHandler getMessageHandler() {
        return messageHandler;
    }

    public void setMessageHandler(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }
}
