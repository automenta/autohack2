package dumb.hack.commands;

import dumb.code.Code;
import dumb.code.CodeUI;
import dumb.code.CodebaseManager;
import dumb.code.CommandManager;
import dumb.code.LMManager;
import dumb.code.MessageHandler;
import dumb.hack.LMOptions;
import dumb.hack.provider.ProviderFactory;
import dumb.hack.provider.MissingApiKeyException;
import dumb.hack.tools.CodeToolProvider;
import dumb.lm.LMClient;
import dumb.mcr.MCR;
import dumb.mcr.Session;
import dev.langchain4j.model.chat.ChatModel;
import picocli.CommandLine;

import java.io.IOException;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "code", mixinStandardHelpOptions = true,
        description = "Runs the interactive AI pair programmer.")
public class CodeCommand implements Callable<Integer> {

    @CommandLine.Option(names = {"--backend"}, description = "Specify the version control backend (git, pijul, or files).")
    private String backend;

    @CommandLine.Option(names = {"--interactive"}, description = "Enable interactive mode.", defaultValue = "true", fallbackValue = "true")
    private boolean interactive;

    @CommandLine.Mixin
    private LMOptions lmOptions;

    @Override
    public Integer call() throws IOException {
        ProviderFactory factory = new ProviderFactory(lmOptions);
        ChatModel model;
        try {
            model = factory.create();
        } catch (MissingApiKeyException e) {
            System.err.println(e.getMessage());
            return 1;
        }

        var aider = aider(model, interactive);
        aider.start();

        return 0;
    }

    private CodeUI aider(ChatModel model, boolean interactive) {
        LMClient lmClient = new LMClient(model);
        LMManager lmManager = new LMManager(lmClient);
        Code code = new Code(backend, null, lmManager);

        CommandManager commandManager = code.commandManager;
        CodebaseManager codebaseManager = code.getCodebaseManager();
        MessageHandler messageHandler = code.getMessageHandler();

        CodeToolProvider toolProvider = new CodeToolProvider(code.fileManager, codebaseManager);

        MCR mcr = new MCR(lmClient);
        Session mcrSession = mcr.createSession(toolProvider);

        ReasonCommand reasonCommand = new ReasonCommand(mcrSession, codebaseManager, messageHandler, code.fileManager, interactive);
        commandManager.registerCommand("/reason", reasonCommand);

        return new CodeUI(code);
    }
}
