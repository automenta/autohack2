package dumb.code.help;

import dumb.code.Code;
import dumb.code.MessageHandler;
import dumb.code.project.ProjectTemplate;
import dumb.code.project.TemplateManager;
import dumb.mcr.MCR;

import java.util.ArrayList;
import java.util.List;

public class DefaultHelpService implements HelpService {

    private TutorialManager tutorialManager;
    private final MCR mcr;
    private MessageHandler messageHandler;
    private final TemplateManager templateManager;
    private Code code;

    public DefaultHelpService(MCR mcr) {
        this.mcr = mcr;
        // Assuming templates are in a "templates" directory at the project root
        this.templateManager = new TemplateManager("templates");
    }

    @Override
    public void setCode(Code code) {
        this.code = code;
    }

    public void setMessageHandler(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    @Override
    public List<String> getHelp() {
        // ... (rest of the getHelp method is unchanged)
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
        // ... (rest of the getHelp(commandName) method is unchanged)
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
    public List<ProjectTemplate> getAvailableTemplates() {
        return templateManager.loadTemplates();
    }

    @Override
    public void startTutorial(ProjectTemplate template) {
        if (messageHandler == null) {
            System.out.println("Error: MessageHandler not set.");
            return;
        }
        if (code == null) {
            System.out.println("Error: Code context not set. Cannot start tutorial.");
            return;
        }
        this.tutorialManager = new TutorialManager(template, mcr, code);
        String message = tutorialManager.start();
        messageHandler.addMessage("system", message);
    }

    @Override
    public void onCommandExecuted(String[] command) {
        if (tutorialManager != null && tutorialManager.isActive()) {
            String message = tutorialManager.checkCommand(command);
            if (message != null) {
                messageHandler.addMessage("system", message);
            }
        }
    }
}
