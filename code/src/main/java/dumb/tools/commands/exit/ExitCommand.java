package dumb.tools.commands.exit;

import dumb.tools.ToolContext;
import dumb.tools.commands.Command;

public record ExitCommand(ToolContext toolContext) implements Command {

    @Override
    public void init() {
        // No initialization needed for ExitCommand
    }

    @Override
    public void execute(String[] args) {
        System.exit(0);
    }

    @Override
    public void cleanup() {
        // No cleanup needed for ExitCommand
    }
}