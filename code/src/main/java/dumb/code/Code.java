package dumb.code;

import dev.langchain4j.service.tool.ToolProvider;
import dumb.code.tui.Terminal;
import dumb.code.util.IProcessRunner;
import dumb.code.util.ProcessRunner;
import dumb.code.versioning.Backend;
import dumb.code.help.DefaultHelpService;
import dumb.code.help.HelpService;
import dumb.lm.LMClient;
import dumb.mcr.MCR;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/** Code-development (coding) context */
public class Code {
    public final List<ToolProvider> toolProviders = new CopyOnWriteArrayList<>();
    public final BackendManager backendManager;
    public final IFileManager fileManager;
    public final LMManager lmManager;
    public final UIManager uiManager;
    public final CommandManager commandManager;
    public final CodebaseManager codebaseManager;
    public final FileSystem files;
    public final MessageHandler messageHandler;
    public final IProcessRunner processRunner;
    private final Backend backend;
    private String diff;
    private Terminal terminal;

    public Code(String backendType, String provider, String model, String apiKey) {
        this(backendType, null, new LMManager(provider, model, apiKey), new DefaultHelpService(new MCR(new LMClient(provider, model, apiKey))));
    }

    public Code(String backendType, IFileManager fileManager, LMManager lmManager, HelpService helpService) {
        this.fileManager = (fileManager != null) ? fileManager : new FileManager();
        this.messageHandler = new MessageHandler(this);
        this.backendManager = new BackendManager(this, this.fileManager);
        this.processRunner = new ProcessRunner();

        if (backendType != null) {
            backendManager.setBackend(backendType);
        } else {
            backendManager.autodetectBackend();
        }
        this.backend = backendManager.getBackend();

        this.lmManager = lmManager;
        this.uiManager = new UIManager(this);
        this.codebaseManager = new CodebaseManager(this);
        this.files = new FileSystem();
        this.commandManager = new CommandManager(this, helpService);

        helpService.setCode(this);
        if (helpService instanceof DefaultHelpService) {
            ((DefaultHelpService) helpService).setMessageHandler(this.messageHandler);
        }
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