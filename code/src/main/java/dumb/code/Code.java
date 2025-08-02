package dumb.code;

import dev.langchain4j.service.tool.ToolProvider;
import dumb.code.help.DefaultHelpService;
import dumb.code.help.HelpService;
import dumb.code.tui.Terminal;
import dumb.code.tui.events.UIEvent;
import dumb.code.util.IProcessRunner;
import dumb.code.util.ProcessRunner;
import dumb.code.versioning.Backend;
import dumb.lm.LMClient;
import dumb.mcr.MCR;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;

/** Code-development (coding) context */
public class Code {
    public final List<ToolProvider> toolProviders = new CopyOnWriteArrayList<>();
    public BackendManager backendManager;
    public IFileManager fileManager;
    public final LMManager lmManager;
    public final UIManager uiManager;
    public final CommandManager commandManager;
    public CodebaseManager codebaseManager;
    public final FileSystem files;
    public final MessageHandler messageHandler;
    public final IProcessRunner processRunner;
    private final Backend backend;
    private String diff;
    private Terminal terminal;
    private final BlockingQueue<UIEvent> eventQueue;

    /**
     * Primary constructor with all dependencies.
     */
    public Code(String backendType, IFileManager fileManager, LMManager lmManager, HelpService helpService, IProcessRunner processRunner, BlockingQueue<UIEvent> eventQueue) {
        this.fileManager = (fileManager != null) ? fileManager : new FileManager();
        this.messageHandler = new MessageHandler(this);
        this.backendManager = new BackendManager(this, this.fileManager);
        this.processRunner = processRunner;
        this.eventQueue = (eventQueue != null) ? eventQueue : new LinkedBlockingQueue<>();

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

        if (helpService != null) {
            helpService.setCode(this);
            if (helpService instanceof DefaultHelpService) {
                ((DefaultHelpService) helpService).setMessageHandler(this.messageHandler);
            }
        }
    }

    /**
     * Constructor for TUI usage.
     */
    public Code(String backendType, IFileManager fileManager, LMManager lmManager, HelpService helpService, BlockingQueue<UIEvent> eventQueue) {
        this(backendType, fileManager, lmManager, helpService, new ProcessRunner(), eventQueue);
    }

    /**
     * Simplified constructor for non-TUI usage.
     */
    public Code(String backendType, String provider, String model, String apiKey) {
        this(backendType, null, new LMManager(provider, model, apiKey), new DefaultHelpService(new MCR(new LMClient(provider, model, apiKey))), new ProcessRunner(), null);
    }

    /**
     * Constructor for tests.
     */
    public Code(String backendType, IFileManager fileManager, LMManager lmManager, HelpService helpService, IProcessRunner processRunner) {
        this(backendType, fileManager, lmManager, helpService, processRunner, null);
    }

    /**
     * General purpose constructor.
     */
    public Code(String backendType, IFileManager fileManager, LMManager lmManager, HelpService helpService) {
        this(backendType, fileManager, lmManager, helpService, new ProcessRunner(), null);
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

    public List<String> getChatFiles() {
        return codebaseManager.getFiles();
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

    public BlockingQueue<UIEvent> getEventQueue() {
        return eventQueue;
    }
}