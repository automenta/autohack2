package dumb.hack.tui;

import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import dev.langchain4j.model.chat.ChatModel;
import dumb.code.Code;
import dumb.hack.App;
import dumb.hack.provider.MissingApiKeyException;
import dumb.hack.provider.ProviderFactory;
import dumb.hack.tui.components.CodePanel;
import dumb.hack.tui.components.McrPanel;
import dumb.lm.LMClient;
import dumb.mcr.MCR;
import dumb.mcr.Session;

import java.io.IOException;
import java.util.Collections;

public class TUI {

    private final App app;
    private final TUIState state;
    private Panel contentPanel;
    private Label statusBar;

    // Panels
    private CodePanel codePanel;
    private McrPanel mcrPanel;


    public TUI(App app) {
        this.app = app;
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
            try {
                ProviderFactory factory = new ProviderFactory(app.getLmOptions());
                ChatModel model = factory.create();
                LMClient lmClient = new LMClient(model);
                Code code = new Code(null, null, new dumb.code.LMManager(lmClient));
                codePanel = new CodePanel(code);
            } catch (MissingApiKeyException e) {
                contentPanel.addComponent(new Label("Error starting Code TUI: " + e.getMessage()));
                return;
            }
        }
        contentPanel.addComponent(codePanel);
        statusBar.setText("Mode: Code");
    }

    private void showMcrPanel() {
        contentPanel.removeAllComponents();
        if (mcrPanel == null) {
            try {
                ProviderFactory factory = new ProviderFactory(app.getLmOptions());
                ChatModel model = factory.create();
                LMClient lmClient = new LMClient(model);
                MCR mcr = new MCR(lmClient);
                Session session = mcr.createSession();
                mcrPanel = new McrPanel(session);
            } catch (MissingApiKeyException e) {
                contentPanel.addComponent(new Label("Error starting MCR TUI: " + e.getMessage()));
                return;
            }
        }
        contentPanel.addComponent(mcrPanel);
        statusBar.setText("Mode: MCR");
    }
}
