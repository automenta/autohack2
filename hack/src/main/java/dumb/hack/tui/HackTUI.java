package dumb.hack.tui;

import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import dumb.hack.App;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class HackTUI {

    private final App app;
    private Panel contentPanel;
    private Label statusBar;
    private final BreadcrumbManager breadcrumbManager;
    private final KeybindingManager keybindingManager;

    private final List<TUIComponent> tuiComponents = new ArrayList<>();
    private final Map<String, Panel> panelCache = new HashMap<>();

    public HackTUI(App app) {
        this.app = app;
        this.breadcrumbManager = new BreadcrumbManager();
        this.keybindingManager = new KeybindingManager();
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
            contentPanel = new Panel();

            RadioBoxList<String> radioBoxList = new RadioBoxList<>();
            for (TUIComponent component : tuiComponents) {
                radioBoxList.addItem(component.getName());
            }
            radioBoxList.addListener((selectedIndex, previousSelection) -> {
                if (selectedIndex >= 0 && selectedIndex < tuiComponents.size()) {
                    showTUI(tuiComponents.get(selectedIndex));
                }
            });

            keybindingManager.register(new KeyStroke('c', true, false, false), () -> radioBoxList.setCheckedItemIndex(0));
            keybindingManager.register(new KeyStroke('m', true, false, false), () -> radioBoxList.setCheckedItemIndex(1));
            keybindingManager.register(new KeyStroke('o', true, false, false), () -> radioBoxList.setCheckedItemIndex(2));
            keybindingManager.register(new KeyStroke('s', true, false, false), () -> statusBar.setText("Saved."));

            window.addWindowListener(new WindowListenerAdapter() {
                @Override
                public void onInput(Window basePane, KeyStroke keyStroke, AtomicBoolean deliverEvent) {
                    if (keybindingManager.handle(keyStroke)) {
                        deliverEvent.set(false);
                    }
                }
            });

            Panel topPanel = new Panel(new LinearLayout(Direction.HORIZONTAL));
            topPanel.addComponent(radioBoxList);
            topPanel.addComponent(new Button("Exit", window::close));

            Panel navigationPanel = new Panel(new LinearLayout(Direction.VERTICAL));
            Label breadcrumbs = new Label("");
            breadcrumbManager.setListener(breadcrumbs::setText);
            navigationPanel.addComponent(topPanel.withBorder(Borders.singleLine("Tabs (Ctrl+C/M/O)")));
            navigationPanel.addComponent(breadcrumbs.withBorder(Borders.singleLine("Breadcrumbs")));

            mainPanel.addComponent(navigationPanel);
            mainPanel.addComponent(contentPanel.withBorder(Borders.singleLine("Content")));

            statusBar = new Label("Ready");
            mainPanel.addComponent(statusBar.withBorder(Borders.singleLine()));

            window.setComponent(mainPanel);

            // Show the first TUI by default
            radioBoxList.setCheckedItemIndex(0);

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
        Panel panel = panelCache.computeIfAbsent(component.getName(), (name) -> component.createPanel(app, breadcrumbManager));
        contentPanel.addComponent(panel);
        statusBar.setText("Mode: " + component.getName());
        breadcrumbManager.setPath(component.getName());
    }
}
