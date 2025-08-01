package dumb.hack.commands;

import dumb.hack.App;
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

    @CommandLine.Option(names = {"--task"}, description = "The task to perform non-interactively.")
    private String task;

    @CommandLine.Option(names = {"--non-interactive"}, description = "Enable non-interactive mode.", defaultValue = "false")
    private boolean nonInteractive;

    @CommandLine.ParentCommand
    private App app;

    @Override
    public Integer call() throws IOException {
        ProviderFactory factory = new ProviderFactory(app.getLmOptions());
        ChatModel model;
        try {
            model = factory.create();
        } catch (MissingApiKeyException e) {
            System.err.println(e.getMessage());
            return 1;
        }

        if (nonInteractive) {
            if (task == null || task.isEmpty()) {
                System.err.println("Error: --task option is required for non-interactive mode.");
                return 1;
            }

            // Setup services but don't start the UI
            CodeServices services = setup(model, false); // false for interactive

            // Execute the reasoning command directly
            services.reasonCommand().execute(new String[]{task});

            System.out.println("Non-interactive task completed.");
            return 0;
        } else {
            // In interactive mode, the HackTUI is responsible for starting the UI.
            // This command is now only for non-interactive mode.
            System.err.println("Interactive mode must be launched from the main `hack` command.");
            return 1;
        }
    }

    private record CodeServices(Code code, ReasonCommand reasonCommand) {}

    private CodeServices setup(ChatModel model, boolean interactive) {
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

        return new CodeServices(code, reasonCommand);
    }

    private CodeUI aider(ChatModel model, boolean interactive) {
        CodeServices services = setup(model, interactive);
        CodeUI codeUI = new CodeUI(services.code());
        // Since we are not in the integrated HackTUI, we create our own screen
        // In the integrated TUI, the screen is managed by HackTUI
        // The UI is now started by HackTUI
        return codeUI;
    }
}
