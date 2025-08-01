package dumb.code.commands.exit;

import dumb.code.Code;
import dumb.code.commands.Command;
import dumb.code.tui.Terminal;

public record ExitCommand(Code code) implements Command {

    @Override
    public void init() {
        // No initialization needed for ExitCommand
    }

    @Override
    public void execute(String[] args) {
        Terminal terminal = code.getTerminal();
        if (terminal != null) {
            terminal.stop();
            code.setTerminal(null);
            code.messageHandler.addMessage("system", "Terminal session ended.");
        }
    }

    @Override
    public void cleanup() {
        // No cleanup needed for ExitCommand
    }
}