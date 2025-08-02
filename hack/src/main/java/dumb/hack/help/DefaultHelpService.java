package dumb.hack.help;

import dumb.code.MessageHandler;
import dumb.mcr.MCR;
import dumb.mcr.Session;

import java.util.ArrayList;
import java.util.List;

/**
 * A default implementation of the HelpService that provides static help messages.
 */
public class DefaultHelpService implements HelpService {

    private final TutorialManager tutorialManager;
    private final MCR mcr;
    private final Session session;
    private MessageHandler messageHandler;

    public DefaultHelpService(MCR mcr) {
        this.tutorialManager = new TutorialManager();
        this.mcr = mcr;
        this.session = mcr.createSession();
    }

    public void setMessageHandler(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    @Override
    public List<String> getHelp() {
        List<String> help = new ArrayList<>();
        help.add("Available commands:");
        help.add("  /help [command] - Show help for a command.");
        help.add("  /add <file>     - Add a file to the chat.");
        help.add("  /diff           - Show the current changes.");
        help.add("  /record <msg>   - Record the current changes.");
        help.add("  /tutorial       - Start the tutorial.");
        help.add("  /exit           - Exit the application.");
        return help;
    }

    @Override
    public List<String> getHelp(String commandName) {
        List<String> help = new ArrayList<>();
        switch (commandName) {
            case "help":
                help.add("Usage: /help [command]");
                help.add("Shows a list of available commands or help for a specific command.");
                break;
            case "add":
                help.add("Usage: /add <file>");
                help.add("Adds a file to the chat so the LLM can see it.");
                break;
            case "diff":
                help.add("Usage: /diff");
                help.add("Shows the current changes to the files in the chat.");
                break;
            case "record":
                help.add("Usage: /record <message>");
                help.add("Records the current changes with a message.");
                break;
            case "tutorial":
                help.add("Usage: /tutorial");
                help.add("Starts an interactive tutorial to learn how to use the system.");
                break;
            case "exit":
                help.add("Usage: /exit");
                help.add("Exits the application.");
                break;
            default:
                help.add("Unknown command: " + commandName);
                help.add("Use /help to see a list of available commands.");
                break;
        }
        return help;
    }

    @Override
    public void startTutorial() {
        if (messageHandler == null) {
            System.out.println("Error: MessageHandler not set.");
            return;
        }
        String message = tutorialManager.start();
        messageHandler.addMessage("system", message);
    }
}
