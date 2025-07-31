package com.pijul.aider;

import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.pijul.aider.tui.Terminal;

import java.io.IOException;

public class PijulAider {
    private final Container container;
    private Terminal tui;

    public PijulAider(Container container) {
        this.container = container;
    }

    public void start() throws IOException {
        // Initialize terminal
        DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory();
        Screen screen = new TerminalScreen(terminalFactory.createTerminal());
        screen.startScreen();

        // Start terminal UI with command manager
        this.tui = new Terminal(screen, container.getCommandManager());
        container.setTerminal(tui); // Set the terminal in the container
        tui.run();
    }

    public void stop() {
        if (tui != null) {
            // It's good practice to have a way to gracefully shut down the TUI
            // For now, we can just stop the command manager's listening aspect
            container.getCommandManager().stopListening();
        }
    }
}
