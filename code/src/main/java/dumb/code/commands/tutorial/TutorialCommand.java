package dumb.code.commands.tutorial;

import dumb.code.Code;
import dumb.code.MessageHandler;
import dumb.code.commands.Command;
import dumb.code.help.HelpService;
import dumb.code.project.ProjectTemplate;

import java.util.List;
import java.util.Optional;

public class TutorialCommand implements Command {

    private final Code code;
    private final HelpService helpService;
    private final MessageHandler messageHandler;

    public TutorialCommand(Code code, HelpService helpService) {
        this.code = code;
        this.helpService = helpService;
        this.messageHandler = code.messageHandler;
    }

    @Override
    public void execute(String[] args) {
        if (args.length == 0) {
            showHelp();
            return;
        }

        String subcommand = args[0].toLowerCase();
        switch (subcommand) {
            case "start":
                startTutorial(args);
                break;
            case "stop":
                stopTutorial();
                break;
            case "list":
                listTemplates();
                break;
            case "help":
            default:
                showHelp();
                break;
        }
    }

    private void startTutorial(String[] args) {
        if (args.length < 2) {
            messageHandler.addMessage("system", "Usage: /tutorial start <template-name>");
            listTemplates();
            return;
        }
        String templateName = args[1];
        Optional<ProjectTemplate> maybeTemplate = helpService.getAvailableTemplates().stream()
                .filter(t -> t.getName().equalsIgnoreCase(templateName))
                .findFirst();

        if (maybeTemplate.isEmpty()) {
            messageHandler.addMessage("system", "Error: Template not found: " + templateName);
            return;
        }
        helpService.startTutorial(maybeTemplate.get());
    }

    private void stopTutorial() {
        helpService.stopTutorial();
    }

    private void listTemplates() {
        messageHandler.addMessage("system", "Available tutorial templates:");
        List<ProjectTemplate> templates = helpService.getAvailableTemplates();
        if (templates.isEmpty()) {
            messageHandler.addMessage("system", "  No templates found.");
        } else {
            for (ProjectTemplate template : templates) {
                messageHandler.addMessage("system", String.format("  - %s: %s", template.getName(), template.getDescription()));
            }
        }
    }

    private void showHelp() {
        messageHandler.addMessage("system", "Usage: /tutorial <subcommand>");
        messageHandler.addMessage("system", "Subcommands:");
        messageHandler.addMessage("system", "  start <template-name> - Start a tutorial.");
        messageHandler.addMessage("system", "  stop                  - Stop the current tutorial.");
        messageHandler.addMessage("system", "  list                  - List available tutorials.");
        messageHandler.addMessage("system", "  help                  - Show this help message.");
    }
}
