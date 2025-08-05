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

    // UI Components
    private UnifiedPanel unifiedPanel;

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
            MultiWindowTextGUI gui = new MultiWindowTextGUI(screen);

            // Loop to handle API key entry and service initialization
            while (true) {
                try {
                    initializeServices();
                    break; // Success, exit the loop
                } catch (MissingApiKeyException e) {
                    String apiKey = showApiKeyDialog(gui);
                    if (apiKey != null && !apiKey.trim().isEmpty()) {
                        app.getLmOptions().setApiKey(apiKey);
                    } else {
                        // User canceled or entered empty key, so we exit
                        return;
                    }
                }
            }

            final BasicWindow window = new BasicWindow("AutoHack - Unified TUI");
            window.setHints(Collections.singletonList(Window.Hint.FULL_SCREEN));

            // This is the main content panel that will hold everything
            final Panel mainContent = new Panel(new LinearLayout(Direction.VERTICAL));

            // Create and set the main UnifiedPanel
            this.unifiedPanel = new UnifiedPanel(this.code, this.mcr);
            // The unified panel should take up most of the space
            mainContent.addComponent(this.unifiedPanel);


            // Create a settings panel for the button
            Panel settingsPanel = new Panel();
            settingsPanel.setLayoutManager(new LinearLayout(Direction.HORIZONTAL));

            Button settingsButton = new Button("Settings", () -> {
                String newApiKey = showApiKeyDialog(gui);
                if (newApiKey != null && !newApiKey.trim().isEmpty()) {
                    // 1. Clean up old resources
                    shutdownServices();
                    mainContent.removeComponent(this.unifiedPanel);

                    // 2. Update config and re-create services
                    app.getLmOptions().setApiKey(newApiKey);
                    try {
                        initializeServices();
                    } catch (MissingApiKeyException ex) {
                        // This shouldn't happen if they just provided a key, but good to handle it.
                        try {
                            com.googlecode.lanterna.gui2.dialogs.MessageDialog.showMessageDialog(gui, "Error", "Failed to initialize with the new API key.");
                        } catch(Exception e) {
                            // ignore, can't show dialog
                        }
                        return; // Exit the lambda
                    }

                    // 3. Re-create the UI panel with the new services
                    this.unifiedPanel = new UnifiedPanel(this.code, this.mcr);
                    mainContent.addComponent(0, this.unifiedPanel);
                }
            });
            settingsPanel.addComponent(settingsButton);
            mainContent.addComponent(settingsPanel);


            window.setComponent(mainContent);

            gui.addWindowAndWait(window);

        } finally {
            if (screen != null) {
                screen.stopScreen();
            }
            shutdownServices();
        }
    }

    /**
     * Initializes the core services of the application (MCR, Code).
     * This method can be called to re-initialize services, for example, after an API key change.
     * @throws MissingApiKeyException if the API key is not configured.
     */
    private void initializeServices() throws MissingApiKeyException {
        ProviderFactory factory = new ProviderFactory(app.getLmOptions());
        ChatModel model = factory.create();
        LMClient lmClient = new LMClient(model);
        this.mcr = new MCR(lmClient);
        this.code = new Code(null, null, new dumb.code.LMManager(lmClient));
    }

    /**
     * Shuts down the services and cleans up resources used by the UI panels.
     */
    private void shutdownServices() {
        if (this.unifiedPanel != null) {
            this.unifiedPanel.close();
        }
    }

    private String showApiKeyDialog(MultiWindowTextGUI gui) {
        final BasicWindow dialogWindow = new BasicWindow("API Key Required");
        dialogWindow.setHints(java.util.Arrays.asList(Window.Hint.MODAL));

        final Panel panel = new Panel(new LinearLayout(Direction.VERTICAL));
        panel.addComponent(new Label("Please enter your API key:"));

        final TextBox apiKeyBox = new TextBox();
        apiKeyBox.setMask('*');
        panel.addComponent(apiKeyBox);

        final String[] resultHolder = new String[1];

        Panel buttonPanel = new Panel(new LinearLayout(Direction.HORIZONTAL));
        buttonPanel.addComponent(new Button("Submit", () -> {
            resultHolder[0] = apiKeyBox.getText();
            dialogWindow.close();
        }));
        buttonPanel.addComponent(new Button("Cancel", dialogWindow::close));
        panel.addComponent(buttonPanel);

        dialogWindow.setComponent(panel);
        gui.addWindowAndWait(dialogWindow);

        return resultHolder[0];
    }
}
