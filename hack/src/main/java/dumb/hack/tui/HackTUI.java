package dumb.hack.tui;

import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import picocli.CommandLine;
import dumb.hack.App;

import java.io.IOException;
import java.util.Collections;

public class HackTUI {

    private final App app;

    public HackTUI(App app) {
        this.app = app;
    }

    public void start() throws IOException {
        DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory();
        Screen screen = null;
        try {
            screen = terminalFactory.createScreen();
            screen.startScreen();

            BasicWindow window = new BasicWindow("Hack Tool Selector");

            Panel contentPanel = new Panel(new LinearLayout(Direction.VERTICAL));
            contentPanel.addComponent(new Label("Select a tool to launch:"));

            Button codeButton = new Button("Code TUI", () -> {
                window.close();
                runCommand("code");
            });
            contentPanel.addComponent(codeButton);

            Button mcrButton = new Button("MCR TUI", () -> {
                window.close();
                runCommand("mcr", "--tui");
            });
            contentPanel.addComponent(mcrButton);

            window.setComponent(contentPanel);
            window.setHints(Collections.singletonList(Window.Hint.CENTERED));

            MultiWindowTextGUI gui = new MultiWindowTextGUI(screen);
            gui.addWindowAndWait(window);

        } finally {
            if (screen != null) {
                screen.stopScreen();
            }
        }
    }

    private void runCommand(String... args) {
        new CommandLine(app).execute(args);
    }
}
