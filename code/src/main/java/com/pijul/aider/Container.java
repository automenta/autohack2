package com.pijul.aider;

import com.pijul.aider.commands.CommandManager;
import com.pijul.aider.tui.Terminal;
import com.pijul.aider.versioning.FileBackend;
import com.pijul.aider.Backend;

public class Container {
    private Backend backend;
    private BackendManager backendManager;
    private FileManager fileManager;
    private LLMManager llmManager;
    private UIManager uiManager;
    private CommandManager commandManager;
    private CodebaseManager codebaseManager;
    private FileSystem fileSystem;
    private String diff;
    private Terminal terminal;
    private MessageHandler messageHandler;

    public Container() {
        this.messageHandler = new MessageHandler(this);
        this.backendManager = new BackendManager();
        this.fileManager = new FileManager();
        this.llmManager = new LLMManager();
        this.uiManager = new UIManager();
        this.codebaseManager = new CodebaseManager(this.backend);
        this.fileSystem = new FileSystem();
        this.commandManager = new CommandManager(this); // Initialize CommandManager after other dependencies
    }

    public MessageHandler getMessageHandler() {
        return messageHandler;
    }

    public Backend getBackend() {
        return this.backend;
    }

    public BackendManager getBackendManager() {
        return backendManager;
    }

    public FileManager getFileManager() {
        return fileManager;
    }

    public LLMManager getLLMManager() {
        return llmManager;
    }

    public UIManager getUIManager() {
        return uiManager;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public CodebaseManager getCodebaseManager() {
        return this.codebaseManager;
    }

    public FileSystem getFileSystem() {
        return fileSystem;
    }

    public String getDiff() {
        return diff;
    }

    public void setDiff(String diff) {
        this.diff = diff;
    }

    public Terminal getTerminal() {
        return terminal;
    }

    public void setTerminal(Terminal terminal) {
        this.terminal = terminal;
    }
}