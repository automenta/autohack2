package dumb.code.commands.createproject;

import dumb.code.Code;
import dumb.code.MessageHandler;
import dumb.code.commands.Command;
import dumb.code.help.HelpService;
import dumb.code.project.ProjectTemplate;

import java.io.File;
import java.util.List;
import java.util.Optional;

public class CreateProjectCommand implements Command {
    private final Code code;
    private final HelpService helpService;

    public CreateProjectCommand(Code code, HelpService helpService) {
        this.code = code;
        this.helpService = helpService;
    }

    @Override
    public void execute(String[] args) {
        MessageHandler messageHandler = code.messageHandler;
        if (args.length == 0) {
            messageHandler.addMessage("system", "Usage: /new <template-name>");
            messageHandler.addMessage("system", "Creates a new project from a template in the current directory.");
            messageHandler.addMessage("system", "It is recommended to create a new directory for your project first, and cd into it.");
            messageHandler.addMessage("system", "Available templates:");
            List<ProjectTemplate> templates = helpService.getAvailableTemplates();
            if (templates.isEmpty()) {
                messageHandler.addMessage("system", "  No templates found.");
            } else {
                for (ProjectTemplate template : templates) {
                    messageHandler.addMessage("system", String.format("  - %s: %s", template.getName(), template.getDescription()));
                }
            }
            return;
        }

        String templateName = args[0];
        Optional<ProjectTemplate> maybeTemplate = helpService.getAvailableTemplates().stream()
                .filter(t -> t.getName().equalsIgnoreCase(templateName))
                .findFirst();

        if (maybeTemplate.isEmpty()) {
            messageHandler.addMessage("system", "Error: Template not found: " + templateName);
            return;
        }

        ProjectTemplate template = maybeTemplate.get();

        try {
            // Create project in the current directory
            File targetDir = new File(System.getProperty("user.dir"));
            helpService.createProject(template, targetDir);
            messageHandler.addMessage("system", "Successfully created project from template: " + template.getName());

            // Automatically start the tutorial
            messageHandler.addMessage("system", "Starting tutorial...");
            helpService.startTutorial(template);

        } catch (Exception e) {
            messageHandler.addMessage("system", "Error creating project: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
