package dumb.hack.commands;

import dumb.code.Code;
import dumb.code.CodeUI;
import dumb.code.CodebaseManager;
import dumb.code.CommandManager;
import dumb.code.MessageHandler;
import dumb.hack.tools.CodeToolProvider;
import dumb.mcr.MCR;
import dumb.mcr.Session;
import picocli.CommandLine;

import java.io.IOException;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "code", mixinStandardHelpOptions = true,
        description = "Runs the interactive AI pair programmer.")
public class CodeCommand implements Callable<Integer> {

    @CommandLine.Option(names = {"--backend"}, description = "Specify the version control backend (git, pijul, or files).")
    private String backend;

    @Override
    public Integer call() throws IOException {
        String provider = System.getProperty("llm.provider", "openai");
        String apiKey = System.getenv(provider.toUpperCase() + "_API_KEY");
        String model = "gpt-4o-mini";

        if (apiKey == null || apiKey.isEmpty()) {
            provider = "mock";
        }

        var aider = aider(provider, model, apiKey);
        aider.start();

        return 0;
    }

    private CodeUI aider(String provider, String model, String apiKey) {
        Code code = new Code(backend, provider, model, apiKey);

        CommandManager commandManager = code.commandManager;
        CodebaseManager codebaseManager = code.getCodebaseManager();
        MessageHandler messageHandler = code.getMessageHandler();

        CodeToolProvider toolProvider = new CodeToolProvider(code.fileManager, codebaseManager);

        MCR mcr = new MCR(provider, model, apiKey);
        Session mcrSession = mcr.createSession(toolProvider);

        ReasonCommand reasonCommand = new ReasonCommand(mcrSession, codebaseManager, messageHandler, code.fileManager);
        commandManager.registerCommand("/reason", reasonCommand);

        return new CodeUI(code);
    }
}
