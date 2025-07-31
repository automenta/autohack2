package com.pijul.aider;

import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.IOException;

public class UIManager {
    private final Screen screen;

    public UIManager(Screen screen) {
        this.screen = screen;
    }

    public void displayWelcomeMessage() {
        try {
            screen.newTextGraphics().putString(0, 0, "Welcome to PijulAider!");
            screen.refresh();
        } catch (IOException e) {
            // Ignore for now
        }
    }

    public void displayMessage(String message) {
        try {
            // This is a very basic implementation. A real implementation would handle scrolling.
            screen.newTextGraphics().putString(0, 1, message);
            screen.refresh();
        } catch (IOException e) {
            // Ignore for now
        }
    }
}