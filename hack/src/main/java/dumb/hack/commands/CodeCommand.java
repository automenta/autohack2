package dumb.hack.commands;

import dev.langchain4j.model.chat.ChatModel;
import dumb.code.*;
import dumb.hack.App;
import dumb.code.help.DefaultHelpService;
import dumb.code.help.HelpService;
import dumb.hack.provider.MissingApiKeyException;
import dumb.hack.provider.ProviderFactory;
import dumb.hack.tools.CodeToolProvider;
import dumb.lm.LMClient;
import dumb.mcr.MCR;
import dumb.mcr.Session;
import picocli.CommandLine;

import java.util.concurrent.Callable;

@CommandLine.Command(name = "code", mixinStandardHelpOptions = true,
        description = "Runs a non-interactive AI pair programming task.")
public class CodeCommand implements Callable<Integer> {

    @CommandLine.Option(names = {"--backend"}, description = "Specify the version control backend (git, pijul, or files).")
    private String backend;

    @CommandLine.Option(names = {"--task"}, required = true, description = "The task to perform.")
    private String task;

    @CommandLine.ParentCommand
    private App app;

    @Override
    public Integer call() {
        ProviderFactory factory = new ProviderFactory(app.getLmOptions());
        ChatModel model;
        try {
            model = factory.create();
        } catch (MissingApiKeyException e) {
            System.err.println(e.getMessage());
            return 1;
        }

        ReasonCommand reasonCommand = createReasonCommand(model);
        reasonCommand.execute(new String[]{task});

        System.out.println("Non-interactive task completed.");
        return 0;
    }

    private ReasonCommand createReasonCommand(ChatModel model) {
        LMClient lmClient = new LMClient(model);
        LMManager lmManager = new LMManager(lmClient);
        MCR mcr = new MCR(lmClient);
        HelpService helpService = new DefaultHelpService(mcr);
        Code code = new Code(backend, null, lmManager, helpService);

        CommandManager commandManager = code.commandManager;
        CodebaseManager codebaseManager = code.getCodebaseManager();
        MessageHandler messageHandler = code.getMessageHandler();

        CodeToolProvider toolProvider = new CodeToolProvider(code.fileManager, codebaseManager);

        Session mcrSession = mcr.createSession(toolProvider);

        ReasonCommand reasonCommand = new ReasonCommand(mcrSession, codebaseManager, messageHandler, code.fileManager, false);
        commandManager.registerCommand("/reason", reasonCommand);

        return reasonCommand;
    }

}
