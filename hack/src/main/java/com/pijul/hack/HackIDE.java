package com.pijul.hack;

import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;

import java.io.IOException;

public class HackIDE {
    private final Container container;
    private TUI tui;

    public HackIDE(Container container) {
        this.container = container;
    }

    public void start() throws IOException {
        // Initialize terminal
        DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory();
        Screen screen = new TerminalScreen(terminalFactory.createTerminal());
        screen.startScreen();

        // Start terminal UI
        this.tui = new TUI(screen, container);
        container.setMessageHandler(tui::addMessage);
        container.init(); // Initialize the container after the message handler is set
        tui.run();
    }

    public void stop() {
        // Graceful shutdown
    }
}
