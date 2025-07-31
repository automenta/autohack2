package dumb.code;

import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import dumb.code.tui.Terminal;

import java.io.IOException;

public class PijulAider {
    private final Context context;
    private Terminal tui;

    public PijulAider(Context context) {
        this.context = context;
    }

    public void start() throws IOException {
        // Initialize terminal
        DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory();
        Screen screen = new TerminalScreen(terminalFactory.createTerminal());
        screen.startScreen();

        // Start terminal UI with command manager
        this.tui = new Terminal(screen, context.commandManager);
        context.setTerminal(tui); // Set the terminal in the container
        tui.run();
    }

    public void stop() {
        if (tui != null) {
            // It's good practice to have a way to gracefully shut down the TUI
            // For now, we can just stop the command manager's listening aspect
            context.commandManager.stopListening();
        }
    }
}
