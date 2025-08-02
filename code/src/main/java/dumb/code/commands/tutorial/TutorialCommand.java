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
        helpService.startTutorial();
    }

    @Override
    public void cleanup() {
        // No cleanup needed
    }
}
