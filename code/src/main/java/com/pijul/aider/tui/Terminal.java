package com.pijul.aider.tui;

import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.pijul.aider.CommandManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Terminal {
    private final List<String> messages = Collections.synchronizedList(new ArrayList<>());
    private final TerminalScreen screen;
    private final CommandManager commandManager;
    private final StringBuilder inputBuffer = new StringBuilder();
    private boolean running = true;

    public Terminal(Screen screen, CommandManager commandManager) throws IOException {
        this.screen = (TerminalScreen) screen;
        this.commandManager = commandManager;
        screen.startScreen();
    }

    public void addMessage(String message) {
        messages.add(message);
    }

    public Screen getScreen() {
        return screen;
    }

    public void run() {
        // Start command input loop
        commandManager.startListening();

        while (running) {
            // Handle terminal input and rendering
            screen.clear();
            TextGraphics tg = screen.newTextGraphics();
            tg.putString(0, 0, "Pijul Aider Terminal");

            // Render messages
            int row = 2;
            synchronized (messages) {
                for (String message : messages) {
                    tg.putString(0, row++, message);
                }
            }

            tg.putString(0, row, "> " + inputBuffer); // Display current input
            try {
                screen.refresh();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Read input and process commands
            com.googlecode.lanterna.input.KeyStroke keyStroke = null;
            try {
                keyStroke = screen.pollInput();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (keyStroke != null) {
                switch (keyStroke.getKeyType()) {
                    case Character:
                        inputBuffer.append(keyStroke.getCharacter());
                        break;
                    case Enter:
                        commandManager.processInput(inputBuffer.toString());
                        inputBuffer.setLength(0); // Clear buffer after processing
                        break;
                    case Backspace:
                        if (!inputBuffer.isEmpty()) {
                            inputBuffer.setLength(inputBuffer.length() - 1);
                        }
                        break;
                    case Escape:
                        running = false; // Exit on Escape
                        break;
                    default:
                        // Ignore other key types for now
                        break;
                }
            }

            try {
                Thread.sleep(50); // Shorter sleep for more responsive input
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                e.printStackTrace();
            }
        }

        // Cleanup
        try {
            screen.stopScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        running = false;
    }
}