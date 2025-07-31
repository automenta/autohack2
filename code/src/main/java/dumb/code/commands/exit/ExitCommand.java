package dumb.code.commands.exit;

import dumb.code.Context;
import dumb.code.commands.Command;
import dumb.code.tui.Terminal;

public record ExitCommand(Context context) implements Command {

    @Override
    public void init() {
        // No initialization needed for ExitCommand
    }

    @Override
    public void execute(String[] args) {
        Terminal terminal = context.getTerminal();
        if (terminal != null) {
            terminal.stop();
            context.setTerminal(null);
            context.messageHandler.addMessage("system", "Terminal session ended.");
        }
    }

    @Override
    public void cleanup() {
        // No cleanup needed for ExitCommand
    }
}