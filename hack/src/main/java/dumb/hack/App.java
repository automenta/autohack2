package dumb.hack;

import dumb.code.CodebaseManager;
import dumb.code.CommandManager;
import dumb.code.Context;
import dumb.code.MessageHandler;
import dumb.code.PijulAider;
import dumb.hack.commands.ReasonCommand;
import dumb.mcr.MCR;
import dumb.mcr.Session;

import java.io.IOException;
import java.util.Properties;

public class App {
    public static void main(String[] args) {
        try {
            // Initialize container for dependency injection
            Context context = new Context(args);

            // --- Integration Logic ---
            // 1. Create MCR instance
            String provider = System.getProperty("llm.provider", "openai");
            // A simple way to get API key. In a real app, use a secure vault.
            String apiKey = System.getenv(provider.toUpperCase() + "_API_KEY");
            String model = "gpt-4o-mini";

            if (apiKey == null || apiKey.isEmpty()) {
                provider = "mock";
            }

            // 2. Get necessary components from autohack's context
            CommandManager commandManager = context.commandManager;
            CodebaseManager codebaseManager = context.getCodebaseManager();
            MessageHandler messageHandler = context.getMessageHandler();

            // Create a ToolProvider with the code modification tools
            dumb.hack.tools.CodeToolProvider toolProvider = new dumb.hack.tools.CodeToolProvider(context.fileManager, codebaseManager);

            MCR mcr = new MCR(provider, model, apiKey);
            Session mcrSession = mcr.createSession(toolProvider);


            // 3. Create and register the ReasonCommand
            ReasonCommand reasonCommand = new ReasonCommand(mcrSession, codebaseManager, messageHandler, context.fileManager);
            commandManager.registerCommand("/reason", reasonCommand);
            // --- End of Integration Logic ---


            // Create and start the main application
            PijulAider aider = new PijulAider(context);
            aider.start();

        } catch (IOException e) {
            System.err.println("Error initializing application: " + e.getMessage());
            e.printStackTrace(); // Also print stack trace for debugging
            System.exit(1);
        }
    }
}
