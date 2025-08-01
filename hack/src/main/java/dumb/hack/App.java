package dumb.hack;

import dumb.code.*;
import dumb.hack.commands.ReasonCommand;
import dumb.mcr.MCR;
import dumb.mcr.Session;
import picocli.CommandLine;

import java.io.IOException;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "hack", mixinStandardHelpOptions = true, version = "hack 1.0",
        description = "AI-powered pair programmer.")
public class App implements Callable<Integer> {

    @CommandLine.Option(names = {"--backend"}, description = "Specify the version control backend (git, pijul, or files).")
    private String backend;

    @CommandLine.Option(names = {"--validate"}, description = "Validate configuration and exit.")
    private boolean validateOnly;

    static void main(String[] args) {
        int exitCode = new CommandLine(new App()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() throws IOException {
        // --- Configuration Validation ---
        String provider = System.getProperty("llm.provider", "openai");
        String apiKey = System.getenv(provider.toUpperCase() + "_API_KEY");
        String model = "gpt-4o-mini";
        boolean isMock = false;

        if (apiKey == null || apiKey.isEmpty()) {
            provider = "mock";
            isMock = true;
        }

        if (validateOnly) {
            System.out.println("Validation successful.");
            if (isMock) {
                System.err.println("Warning: No API key found for provider '" + System.getProperty("llm.provider", "openai") + "'. Falling back to mock LLM provider.");
            } else {
                System.out.println("API key for provider '" + provider + "' is configured.");
            }
            return 0;
        }

        // --- Application Startup ---
        // Initialize container for dependency injection
        var aider = aider(provider, model, apiKey);
        aider.start();

        return 0;
    }

    private CodeUI aider(String provider, String model, String apiKey) {
        Code code = new Code(backend, provider, model, apiKey);

        // --- Integration Logic ---
        // 2. Get necessary components from autohack's context
        CommandManager commandManager = code.commandManager;
        CodebaseManager codebaseManager = code.getCodebaseManager();
        MessageHandler messageHandler = code.getMessageHandler();

        // Create a ToolProvider with the code modification tools
        dumb.hack.tools.CodeToolProvider toolProvider = new dumb.hack.tools.CodeToolProvider(code.fileManager, codebaseManager);

        MCR mcr = new MCR(provider, model, apiKey);
        Session mcrSession = mcr.createSession(toolProvider);

        // 3. Create and register the ReasonCommand
        ReasonCommand reasonCommand = new ReasonCommand(mcrSession, codebaseManager, messageHandler, code.fileManager);
        commandManager.registerCommand("/reason", reasonCommand);
        // --- End of Integration Logic ---

        // Create and start the main application
        return new CodeUI(code);
    }
}
