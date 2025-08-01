package dumb.code;

import dev.langchain4j.service.tool.ToolProvider;
import dumb.code.tui.Terminal;
import dumb.code.util.IProcessRunner;
import dumb.code.util.ProcessRunner;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Context {
    public final List<ToolProvider> toolProviders = new CopyOnWriteArrayList<>();
    public final BackendManager backendManager;
    public final FileManager fileManager;
    public final LMManager LMManager;
    public final UIManager uiManager;
    public final CommandManager commandManager;
    public final CodebaseManager codebaseManager;
    public final FileSystem files;
    public final MessageHandler messageHandler;
    public final IProcessRunner processRunner;
    private Backend backend;
    private String diff;
    private Terminal terminal;

    public Context(String[] args) {
        this(args, null);
    }

    public Context(String[] args, FileManager fileManager, MessageHandler messageHandler, LMManager lmManager, IProcessRunner processRunner) {
        this.messageHandler = (messageHandler != null) ? messageHandler : new MessageHandler(this);
        this.backendManager = new BackendManager(this);
        this.processRunner = (processRunner != null) ? processRunner : new ProcessRunner();

        initializeBackend(args);

        this.fileManager = (fileManager != null) ? fileManager : new FileManager();
        this.LMManager = (lmManager != null) ? lmManager : new LMManager();
        this.uiManager = new UIManager(this);
        this.codebaseManager = new CodebaseManager(this);
        this.files = new FileSystem();
        this.commandManager = new CommandManager(this); // Initialize CommandManager after other dependencies
    }

    public Context(String[] args, FileManager fileManager, MessageHandler messageHandler, LMManager lmManager) {
        this(args, fileManager, messageHandler, lmManager, null);
    }

    public Context(String[] args, FileManager fileManager, MessageHandler messageHandler) {
        this(args, fileManager, messageHandler, null, null);
    }

    public Context(String[] args, FileManager fileManager) {
        this(args, fileManager, null, null, null);
    }

    private void initializeBackend(String[] args) {
        String backendType = parseBackendFromArgs(args);
        if (backendType != null) {
            backendManager.setBackend(backendType);
        } else {
            backendManager.autodetectBackend();
        }
        this.backend = backendManager.getBackend();
    }

    private String parseBackendFromArgs(String[] args) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("--backend") && i + 1 < args.length) {
                return args[i + 1];
            }
        }
        return null;
    }

    public Backend getBackend() {
        return this.backend;
    }

    public MessageHandler getMessageHandler() {
        return messageHandler;
    }

    public CodebaseManager getCodebaseManager() {
        return codebaseManager;
    }

    public FileSystem getFiles() {
        return files;
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