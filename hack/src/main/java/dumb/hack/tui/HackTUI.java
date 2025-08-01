package dumb.hack.tui;

import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import dev.langchain4j.model.chat.ChatModel;
import dumb.code.Code;
import dumb.code.CodeUI;
import dumb.hack.App;
import dumb.hack.provider.MissingApiKeyException;
import dumb.hack.provider.ProviderFactory;
import dumb.lm.LMClient;
import dumb.mcr.MCR;
import dumb.mcr.McrTUI;
import dumb.mcr.Session;

import java.io.IOException;
import java.util.Collections;

public class HackTUI {

    private final App app;
    private Panel contentPanel;
    private Label statusBar;

    // Cache for the UI components
    private CodeUI codeUI;
    private McrTUI mcrTUI;
    private Panel codePanel;
    private Panel mcrPanel;


    public HackTUI(App app) {
        this.app = app;
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
            topPanel.addComponent(new Button("Code", this::showCodeTUI));
            topPanel.addComponent(new Button("MCR", this::showMcrTUI));
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

    private void showCodeTUI() {
        contentPanel.removeAllComponents();
        if (codeUI == null) {
            try {
                ProviderFactory factory = new ProviderFactory(app.getLmOptions());
                ChatModel model = factory.create();
                LMClient lmClient = new LMClient(model);
                Code code = new Code(null, null, new dumb.code.LMManager(lmClient));
                codeUI = new CodeUI(code);
                codePanel = codeUI.createPanel();
            } catch (MissingApiKeyException e) {
                contentPanel.addComponent(new Label("Error starting Code TUI: " + e.getMessage()));
                return;
            }
        }
        contentPanel.addComponent(codePanel);
        statusBar.setText("Mode: Code");
    }

    private void showMcrTUI() {
        contentPanel.removeAllComponents();
        if (mcrTUI == null) {
            try {
                ProviderFactory factory = new ProviderFactory(app.getLmOptions());
                ChatModel model = factory.create();
                LMClient lmClient = new LMClient(model);
                MCR mcr = new MCR(lmClient);
                Session session = mcr.createSession();
                session.assertProlog("is_a(tweety, canary).");
                session.assertProlog("bird(X) :- is_a(X, canary).");
                session.assertProlog("has_wings(X) :- bird(X).");
                session.addRelationship("tweety", "likes", "seeds");

                mcrTUI = new McrTUI(session);
                mcrPanel = mcrTUI.createPanel();
            } catch (MissingApiKeyException e) {
                contentPanel.addComponent(new Label("Error starting MCR TUI: " + e.getMessage()));
                return;
            }
        }
        contentPanel.addComponent(mcrPanel);
        statusBar.setText("Mode: MCR");
    }
}
