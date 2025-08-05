package dumb.code;

import dev.langchain4j.service.tool.ToolProvider;
import dumb.code.util.IProcessRunner;
import dumb.code.util.ProcessRunner;
import dumb.code.versioning.Backend;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/** Code-development (coding) context */
public class Code {
    public final List<ToolProvider> toolProviders = new CopyOnWriteArrayList<>();
    public final BackendManager backendManager;
    public final IFileManager fileManager;
    public final LMManager lmManager;
    public final CommandManager commandManager;
    public final CodebaseManager codebaseManager;
    public final FileSystem files;
    public final MessageHandler messageHandler;
    public final IProcessRunner processRunner;
    private final Backend backend;
    private String diff;

    public Code(String backendType, String provider, String model, String apiKey) {
        this(backendType, null, new LMManager(provider, model, apiKey));
    }

    public Code(String backendType, IFileManager fileManager, LMManager lmManager) {
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
        this.codebaseManager = new CodebaseManager(this);
        this.files = new FileSystem();
        this.commandManager = new CommandManager(this);
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

}