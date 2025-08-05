package dumb.hack.tui.components;

import com.googlecode.lanterna.gui2.*;
import dumb.hack.HackController;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CodePanel extends Panel {

    private final HackController controller;
    private final TextBox inputBox;
    private final Label outputBox;
    private final ExecutorService executor;

    public CodePanel(HackController controller) {
        super(new LinearLayout(Direction.VERTICAL));
        this.controller = controller;
        this.executor = Executors.newSingleThreadExecutor();

        this.outputBox = new Label("Welcome to the Code view!");
        this.inputBox = new TextBox();

        Panel inputPanel = new Panel(new LinearLayout(Direction.HORIZONTAL));
        inputPanel.addComponent(inputBox);
        inputPanel.addComponent(new Button("Submit", this::handleSubmit));

        this.addComponent(outputBox.withBorder(Borders.singleLine("Output")));
        this.addComponent(inputPanel.withBorder(Borders.singleLine("Input")));

        controller.getModel().setCodeOutputListener(outputBox::setText);
    }

    private void handleSubmit() {
        String input = inputBox.getText();
        if (input != null && !input.trim().isEmpty()) {
            // Run the command on a background thread
            executor.submit(() -> {
                controller.processCodeInput(input);
            });
            inputBox.setText("");
        }
    }


    // It's good practice to shut down the executor when the panel is no longer needed.
    public void close() {
        executor.shutdown();
    }
}
