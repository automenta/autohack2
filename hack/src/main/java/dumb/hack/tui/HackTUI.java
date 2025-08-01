package dumb.hack.tui;

import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import dumb.code.Code;
import dumb.code.CodeUI;
import dumb.code.tui.TerminalPanel;
import dumb.hack.App;
import dumb.hack.provider.MissingApiKeyException;
import dumb.hack.provider.ProviderFactory;
import dumb.lm.LMClient;
import dumb.mcr.MCR;
import dumb.mcr.McrTUI;
import dumb.mcr.Session;
import dev.langchain4j.model.chat.ChatModel;

import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;

public class HackTUI {

    private final App app;
    private Panel contentPanel;
    private TerminalPanel codeTerminalPanel;

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

            window.setComponent(mainPanel);

            window.addWindowListener(new WindowListenerAdapter() {
                @Override
                public void onInput(Window basePane, KeyStroke keyStroke, AtomicBoolean hasBeenHandled) {
                    if (codeTerminalPanel != null) {
                        handleKeyStroke(keyStroke);
                        hasBeenHandled.set(true);
                    }
                }
            });

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
        codeTerminalPanel = null;
        try {
            ProviderFactory factory = new ProviderFactory(app.getLmOptions());
            ChatModel model = factory.create();
            LMClient lmClient = new LMClient(model);
            Code code = new Code(null, null, new dumb.code.LMManager(lmClient));
            CodeUI codeUI = new CodeUI(code);
            Panel codePanel = codeUI.createPanel();
            codeTerminalPanel = codeUI.getTerminal().getTerminalPanel();
            contentPanel.addComponent(codePanel);
        } catch (MissingApiKeyException e) {
            contentPanel.addComponent(new Label("Error starting Code TUI: " + e.getMessage()));
        }
    }

    private void showMcrTUI() {
        contentPanel.removeAllComponents();
        codeTerminalPanel = null;
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

            McrTUI mcrTUI = new McrTUI(session);
            contentPanel.addComponent(mcrTUI.createPanel());
        } catch (MissingApiKeyException e) {
            contentPanel.addComponent(new Label("Error starting MCR TUI: " + e.getMessage()));
        }
    }

    private void handleKeyStroke(KeyStroke keyStroke) {
        if (codeTerminalPanel == null) {
            return;
        }
        if (keyStroke.getKeyType() == KeyType.Enter) {
            codeTerminalPanel.processInput(codeTerminalPanel.getInput());
            codeTerminalPanel.clearInput();
        }
    }
}
