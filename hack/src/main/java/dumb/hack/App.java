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
            Properties mcrProps = new Properties();
            String provider = System.getProperty("llm.provider", "openai");
            // A simple way to get API key. In a real app, use a secure vault.
            String apiKey = System.getenv(provider.toUpperCase() + "_API_KEY");

            if (apiKey == null || apiKey.isEmpty()) {
                mcrProps.setProperty("llm.provider", "mock");
            } else {
                mcrProps.setProperty("llm.provider", provider);
                mcrProps.setProperty("llm.apiKey", apiKey);
            }
            mcrProps.setProperty("llm.model", "gpt-4o-mini");
            MCR mcr = new MCR(mcrProps);
            Session mcrSession = mcr.createSession();

            // 2. Get necessary components from autohack's context
            CommandManager commandManager = context.commandManager;
            CodebaseManager codebaseManager = context.getCodebaseManager();
            MessageHandler messageHandler = context.getMessageHandler();

            // 3. Create and register the ReasonCommand
            ReasonCommand reasonCommand = new ReasonCommand(mcrSession, codebaseManager, messageHandler);
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
