package dumb.hack.agent;

import dumb.code.agent.ToolRegistry;
import dumb.code.CommandManager;
import dumb.code.MessageHandler;
import dumb.code.help.DefaultHelpService;
import dumb.code.help.HelpService;
import dumb.code.help.TutorialManagerFactory;
import dumb.code.tools.CodebaseTool;
import dumb.code.tools.FileSystemTool;
import dumb.code.tools.VersionControlTool;
import dumb.code.util.IProcessRunner;
import dumb.code.util.ProcessRunner;
import dumb.hack.help.TutorialManagerFactoryImpl;
import dumb.lm.LMClient;
import dumb.mcr.MCR;
import dumb.mcr.Session;
import dumb.lm.LMManager;

import java.util.List;

/**
 * The central orchestrator for the AI agent.
 * This class is responsible for managing the agent's lifecycle,
 * coordinating between the reasoning engine (MCR) and the available tools.
 */
public class AgentOrchestrator {

    private final ToolRegistry toolRegistry;
    private final CommandManager commandManager;
    private final MessageHandler messageHandler;

    public AgentOrchestrator(String projectRoot, LMManager lmManager) {
        this.toolRegistry = new ToolRegistry();
        this.messageHandler = new MessageHandler();

        // Create tools
        FileSystemTool fileSystemTool = new FileSystemTool(projectRoot);
        VersionControlTool versionControlTool = new VersionControlTool(projectRoot);
        CodebaseTool codebaseTool = new CodebaseTool(versionControlTool, fileSystemTool);

        // Register tools
        toolRegistry.register(fileSystemTool);
        toolRegistry.register(codebaseTool);
        toolRegistry.register(versionControlTool);

        // Create other services
        IProcessRunner processRunner = new ProcessRunner();
        MCR mcr = new MCR(new LMClient(lmManager.getProvider(), lmManager.getModel(), lmManager.getApiKey()));
        TutorialManagerFactory tutorialManagerFactory = new TutorialManagerFactoryImpl(mcr);
        HelpService helpService = new DefaultHelpService(messageHandler, tutorialManagerFactory);
        dumb.mcr.tools.DefaultToolProvider toolProvider = new dumb.mcr.tools.DefaultToolProvider();
        Session session = mcr.createSession(toolProvider);
        toolProvider.setSession(session);

        // Create CommandManager
        this.commandManager = new CommandManager(messageHandler, helpService, codebaseTool, versionControlTool, processRunner, lmManager, fileSystemTool);
        this.commandManager.registerCommand("prolog", new dumb.hack.commands.prolog.PrologCommand(session, messageHandler));
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public List<Object> getTools() {
        return toolRegistry.getAllTools();
    }
}
