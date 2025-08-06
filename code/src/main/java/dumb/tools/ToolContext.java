package dumb.tools;

import dev.langchain4j.service.tool.ToolProvider;
import dumb.tools.util.IProcessRunner;
import dumb.tools.util.ProcessRunner;
import dumb.tools.versioning.Backend;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/** Tooling context */
public class ToolContext {
    public final List<ToolProvider> toolProviders = new CopyOnWriteArrayList<>();
    public final BackendManager backendManager;
    public final IFileManager fileManager;
    public final LMManager lmManager;
    public final CommandManager commandManager;
    public final Workspace workspace;
    public final FileSystem files;
    public final MessageHandler messageHandler;
    public final IProcessRunner processRunner;
    private final Backend backend;
    private String diff;

    public ToolContext(String backendType, String provider, String model, String apiKey) {
        this(backendType, null, new LMManager(provider, model, apiKey));
    }

    public ToolContext(String backendType, IFileManager fileManager, LMManager lmManager) {
        this.fileManager = (fileManager != null) ? fileManager : new FileManager();
        this.messageHandler = new MessageHandler();
        this.backendManager = new BackendManager(this, this.fileManager);
        this.processRunner = new ProcessRunner();

        if (backendType != null) {
            backendManager.setBackend(backendType);
        } else {
            backendManager.autodetectBackend();
        }
        this.backend = backendManager.getBackend();

        this.lmManager = lmManager;
        this.workspace = new Workspace(this);
        this.files = new FileSystem();
        this.commandManager = new CommandManager(this);
    }

    public Backend getBackend() {
        return this.backend;
    }

    public MessageHandler getMessageHandler() {
        return messageHandler;
    }

    public Workspace getWorkspace() {
        return workspace;
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

}