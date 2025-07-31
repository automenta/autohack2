package dumb.hack;

import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.screen.Screen;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TUI implements Runnable {
    private final Screen screen;
    private final HackContext context;
    private final List<String> messages = Collections.synchronizedList(new ArrayList<>());
    private final StringBuilder inputBuffer = new StringBuilder();
    private boolean running = true;

    public TUI(Screen screen, HackContext context) {
        this.screen = screen;
        this.context = context;
    }

    public void addMessage(String message) {
        messages.add(message);
    }

    @Override
    public void run() {
        context.init();

        while (running) {
            try {
                screen.clear();
                TextGraphics tg = screen.newTextGraphics();
                tg.putString(0, 0, "Hack IDE");

                // Render messages
                int row = 2;
                synchronized (messages) {
                    for (String message : messages) {
                        tg.putString(0, row++, message);
                    }
                }

                tg.putString(0, row, "> " + inputBuffer);
                screen.refresh();

                com.googlecode.lanterna.input.KeyStroke keyStroke = screen.pollInput();
                if (keyStroke != null) {
                    switch (keyStroke.getKeyType()) {
                        case Character:
                            inputBuffer.append(keyStroke.getCharacter());
                            break;
                        case Enter:
                            context.getCommands().processInput(inputBuffer.toString());
                            inputBuffer.setLength(0);
                            break;
                        case Backspace:
                            if (!inputBuffer.isEmpty()) {
                                inputBuffer.setLength(inputBuffer.length() - 1);
                            }
                            break;
                        case Escape:
                            running = false;
                            break;
                        default:
                            break;
                    }
                }

                Thread.sleep(50);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
                running = false;
            }
        }

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
