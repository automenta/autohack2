package dumb.hack;

import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;

import java.io.IOException;

public class HackIDE {
    private final HackContext context;
    private TUI tui;

    public HackIDE(HackContext context) {
        this.context = context;
    }

    public void start() throws IOException {
        // Initialize terminal
        DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory();
        Screen screen = new TerminalScreen(terminalFactory.createTerminal());
        screen.startScreen();

        // Start terminal UI
        this.tui = new TUI(screen, context);
        context.setMessageHandler(tui::addMessage);
        context.init(); // Initialize the container after the message handler is set
        tui.run();
    }

    public void stop() {
        // Graceful shutdown
    }
}
