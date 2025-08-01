package dumb.code;

import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import dumb.code.tui.Terminal;

import java.io.IOException;

public class CodeUI {
    private final Code code;
    private Terminal tui;

    public CodeUI(Code code) {
        this.code = code;
    }

    public void start() throws IOException {

        var screen = new TerminalScreen(new DefaultTerminalFactory().createTerminal());
        screen.startScreen();

        // Start terminal UI with command manager
        this.tui = new Terminal(screen, code.commandManager);
        code.setTerminal(tui); // Set the terminal in the container
        tui.run();
    }

    public void stop() {
        if (tui != null) {
            // It's good practice to have a way to gracefully shut down the TUI
            // For now, we can just stop the command manager's listening aspect
            code.commandManager.stopListening();
        }
    }
}
