package dumb.code.commands.exit;

import dumb.code.commands.Command;

public record ExitCommand() implements Command {

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