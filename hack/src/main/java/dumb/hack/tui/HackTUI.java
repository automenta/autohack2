package dumb.hack.tui;

import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import dumb.hack.App;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HackTUI {

    private final App app;
    private Panel contentPanel;
    private Label statusBar;

    private final List<TUIComponent> tuiComponents = new ArrayList<>();
    private final Map<String, Panel> panelCache = new HashMap<>();

    public HackTUI(App app) {
        this.app = app;
        tuiComponents.add(new CodeTUIComponent());
        tuiComponents.add(new McrTUIComponent());
        tuiComponents.add(new ConfigTUIComponent());
    }

    public void start() throws IOException {
        DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory();
        Screen screen = null;
        try {
            screen = terminalFactory.createScreen();
            screen.startScreen();

            BasicWindow window = new BasicWindow("Hack");
            window.setHints(Collections.singletonList(Window.Hint.FULL_SCREEN));

            Panel mainPanel = new Panel(new LinearLayout(Direction.VERTICAL));

            Panel topPanel = new Panel(new LinearLayout(Direction.HORIZONTAL));
            for (TUIComponent component : tuiComponents) {
                topPanel.addComponent(new Button(component.getName(), () -> showTUI(component)));
            }
            topPanel.addComponent(new Button("Exit", window::close));
            mainPanel.addComponent(topPanel.withBorder(Borders.singleLine()));

            contentPanel = new Panel();
            mainPanel.addComponent(contentPanel.withBorder(Borders.singleLine("Content")));

            statusBar = new Label("Ready");
            mainPanel.addComponent(statusBar.withBorder(Borders.singleLine()));

            window.setComponent(mainPanel);

            MultiWindowTextGUI gui = new MultiWindowTextGUI(screen);
            gui.addWindowAndWait(window);

        } finally {
            if (screen != null) {
                screen.stopScreen();
            }
        }
    }

    private void showTUI(TUIComponent component) {
        contentPanel.removeAllComponents();
        Panel panel = panelCache.computeIfAbsent(component.getName(), (name) -> component.createPanel(app));
        contentPanel.addComponent(panel);
        statusBar.setText("Mode: " + component.getName());
    }
}
