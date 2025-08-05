package dumb.hack.ui.tui;

import com.googlecode.lanterna.gui2.*;

public class TerminalPanel extends Panel {
    private final TextBox inputBox;
    private final Label outputBox;

    public TerminalPanel(Runnable onInputSubmit) {
        super(new LinearLayout(Direction.VERTICAL));
        this.outputBox = new Label("Welcome!");
        this.inputBox = new TextBox();

        Panel inputPanel = new Panel(new LinearLayout(Direction.HORIZONTAL));
        // Remove the problematic layout call for now to ensure compilation
        inputPanel.addComponent(inputBox);
        inputPanel.addComponent(new Button("Submit", onInputSubmit));

        this.addComponent(outputBox.withBorder(Borders.singleLine("Output")));
        this.addComponent(inputPanel.withBorder(Borders.singleLine("Input")));
    }

    public String getInputText() {
        return inputBox.getText();
    }

    public void clearInput() {
        inputBox.setText("");
    }

    public void setOutputText(String text) {
        this.outputBox.setText(text);
    }
}
