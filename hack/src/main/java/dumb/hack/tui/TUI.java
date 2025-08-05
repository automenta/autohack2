package dumb.hack.tui;

import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import dev.langchain4j.model.chat.ChatModel;
import dumb.code.Code;
import dumb.hack.App;
import dumb.hack.provider.MissingApiKeyException;
import dumb.hack.provider.ProviderFactory;
import dumb.hack.tui.components.UnifiedPanel;
import dumb.lm.LMClient;
import dumb.mcr.MCR;

import java.io.IOException;
import java.util.Collections;

public class TUI {

    private final App app;
    private final TUIState state;

    // Shared resources
    private Code code;
    private MCR mcr;

    public TUI(App app) {
        this.app = app;
        this.state = new TUIState();
    }

    public void start() throws IOException {
        DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory();
        Screen screen = null;
        UnifiedPanel unifiedPanel = null; // Declare here for the finally block
        try {
            screen = terminalFactory.createScreen();
            screen.startScreen();

            // Initialize shared resources
            try {
                ProviderFactory factory = new ProviderFactory(app.getLmOptions());
                ChatModel model = factory.create();
                LMClient lmClient = new LMClient(model);
                this.mcr = new MCR(lmClient);
                this.code = new Code(null, null, new dumb.code.LMManager(lmClient));
            } catch (MissingApiKeyException e) {
                System.err.println("Error: " + e.getMessage());
                // In a future step, we'll show a proper dialog.
                return;
            }


            BasicWindow window = new BasicWindow("AutoHack - Unified TUI");
            window.setHints(Collections.singletonList(Window.Hint.FULL_SCREEN));

            // Create and set the main UnifiedPanel
            unifiedPanel = new UnifiedPanel(this.code, this.mcr);
            window.setComponent(unifiedPanel);

            MultiWindowTextGUI gui = new MultiWindowTextGUI(screen);
            gui.addWindowAndWait(window);

        } finally {
            if (screen != null) {
                screen.stopScreen();
            }
            if (unifiedPanel != null) {
                unifiedPanel.close(); // Clean up resources
            }
        }
    }
}
