package dumb.code.commands.tutorial;

import dumb.code.commands.Command;
import dumb.code.help.HelpService;

public class TutorialCommand implements Command {

    private final HelpService helpService;

    public TutorialCommand(HelpService helpService) {
        this.helpService = helpService;
    }

    @Override
    public void init() {
        // No initialization needed
    }

    @Override
    public void execute(String[] args) {
        var templates = helpService.getAvailableTemplates();
        if (templates.isEmpty()) {
            // How to communicate this to the user?
            // This needs a proper UI integration. For now, just printing to console.
            System.out.println("No tutorial templates found.");
            return;
        }
        // For now, just start the first available tutorial.
        // A real implementation should prompt the user.
        helpService.startTutorial(templates.get(0));
    }

    @Override
    public void cleanup() {
        // No cleanup needed
    }
}
