package dumb.hack.tui;

import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import dumb.hack.HackController;
import dumb.hack.tui.components.CodePanel;
import dumb.hack.tui.components.McrPanel;

import java.io.IOException;
import java.util.Collections;

public class TUI {

    private final HackController controller;
    private final TUIState state;
    private Panel contentPanel;
    private Label statusBar;

    // Panels
    private CodePanel codePanel;
    private McrPanel mcrPanel;


    public TUI(HackController controller) {
        this.controller = controller;
        this.state = new TUIState();
    }

    public void start() throws IOException {
        DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory();
        Screen screen = null;
        try {
            screen = terminalFactory.createScreen();
            screen.startScreen();

            BasicWindow window = new BasicWindow("Hack");
            window.setHints(Collections.singletonList(Window.Hint.FULL_SCREEN));

            Panel mainPanel = new Panel(new BorderLayout());

            // Navigation
            Panel navigationPanel = new Panel(new LinearLayout(Direction.HORIZONTAL));
            navigationPanel.addComponent(new Button("Code", this::showCodePanel));
            navigationPanel.addComponent(new Button("MCR", this::showMcrPanel));
            navigationPanel.addComponent(new Button("Exit", window::close));
            mainPanel.addComponent(navigationPanel.withBorder(Borders.singleLine()), BorderLayout.Location.TOP);

            // Content
            contentPanel = new Panel();
            mainPanel.addComponent(contentPanel.withBorder(Borders.singleLine("Content")), BorderLayout.Location.CENTER);

            // Status bar
            statusBar = new Label("Status: Ready");
            mainPanel.addComponent(statusBar.withBorder(Borders.singleLine()), BorderLayout.Location.BOTTOM);

            window.setComponent(mainPanel);

            MultiWindowTextGUI gui = new MultiWindowTextGUI(screen);
            gui.addWindowAndWait(window);

        } finally {
            if (screen != null) {
                screen.stopScreen();
            }
            if (codePanel != null) {
                codePanel.close();
            }
            if (mcrPanel != null) {
                mcrPanel.close();
            }
        }
    }

    private void showCodePanel() {
        contentPanel.removeAllComponents();
        if (codePanel == null) {
            codePanel = new CodePanel(controller);
        }
        contentPanel.addComponent(codePanel);
        statusBar.setText("Mode: Code");
    }

    private void showMcrPanel() {
        contentPanel.removeAllComponents();
        if (mcrPanel == null) {
            mcrPanel = new McrPanel(controller);
        }
        contentPanel.addComponent(mcrPanel);
        statusBar.setText("Mode: MCR");
    }
}
