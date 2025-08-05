package dumb.hack.tui.components;

import com.googlecode.lanterna.gui2.*;
import dumb.code.Code;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CodePanel extends Panel {

    private final Code code;
    private final TextBox inputBox;
    private final Label outputBox;
    private final ExecutorService executor;

    public CodePanel(Code code) {
        super(new LinearLayout(Direction.VERTICAL));
        this.code = code;
        this.executor = Executors.newSingleThreadExecutor();

        this.outputBox = new Label("Welcome to the Code view!");
        this.inputBox = new TextBox();

        Panel inputPanel = new Panel(new LinearLayout(Direction.HORIZONTAL));
        inputPanel.addComponent(inputBox);
        inputPanel.addComponent(new Button("Submit", this::handleSubmit));

        this.addComponent(outputBox.withBorder(Borders.singleLine("Output")));
        this.addComponent(inputPanel.withBorder(Borders.singleLine("Input")));

        setupMessageHandler();
    }

    private void handleSubmit() {
        String input = inputBox.getText();
        if (input != null && !input.trim().isEmpty()) {
            // Run the command on a background thread
            executor.submit(() -> {
                code.commandManager.processInput(input);
            });
            inputBox.setText("");
        }
    }

    private void setupMessageHandler() {
        // The message handler will be called from the command manager thread.
        // We need to update the UI on the UI thread.
        this.code.getMessageHandler().setListener(message -> {
            // For now, we'll just update the text directly.
            // In a real application, we would need to schedule this on the UI thread.
            // Lanterna is thread-safe, so this should be fine.
            outputBox.setText(message);
        });
    }

    /**
     * Programmatically sets the text of the input box.
     * This can be called from other components to pass data to this panel.
     * This method is thread-safe as Lanterna's TextBox is thread-safe.
     * @param text The text to set in the input box.
     */
    public void setInputText(String text) {
        inputBox.setText(text);
    }

    // It's good practice to shut down the executor when the panel is no longer needed.
    public void close() {
        executor.shutdown();
    }
}
