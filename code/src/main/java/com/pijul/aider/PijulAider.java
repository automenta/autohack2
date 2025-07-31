package com.pijul.aider;

public class PijulAider {
    private final BackendManager backendManager;
    private final CodebaseManager codebaseManager;
    private final LLMManager llmManager;
    private final CommandManager commandManager;
    private final UIManager uiManager;

    public PijulAider(UIManager uiManager) {
        this.uiManager = uiManager;
        this.backendManager = new BackendManager();
        this.codebaseManager = new CodebaseManager(backendManager.getBackend());
        this.llmManager = new LLMManager();
        this.commandManager = new CommandManager(this);
    }

    public void start() {
        uiManager.displayWelcomeMessage();
        // The command manager will be started by the TUI
    }

    public void stop() {
        // Stop PijulAider
        backendManager.shutdown();
    }

    public BackendManager getBackendManager() {
        return backendManager;
    }

    public CodebaseManager getCodebaseManager() {
        return codebaseManager;
    }

    public LLMManager getLlmManager() {
        return llmManager;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public UIManager getUiManager() {
        return uiManager;
    }
}