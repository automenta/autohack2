package dumb.code;

import com.googlecode.lanterna.gui2.Panel;
import dumb.code.tui.Terminal;

public class CodeUI {
    private final CommandManager commandManager;
    private Terminal tui;

    public CodeUI(CommandManager commandManager) {
        this.commandManager = commandManager;
    }

    public Panel createPanel() {
        this.tui = new Terminal(commandManager);
        // The terminal doesn't need to be set on a container anymore
        return tui.getTerminalPanel();
    }

    public Terminal getTerminal() {
        return tui;
    }

    public void stop() {
        if (tui != null) {
            // It's good practice to have a way to gracefully shut down the TUI
            // For now, we can just stop the command manager's listening aspect
            commandManager.stopListening();
        }
    }
}
