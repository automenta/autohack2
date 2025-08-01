package dumb.code.tui;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.Borders;
import com.googlecode.lanterna.gui2.LinearLayout;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.TextBox;
import dumb.code.CommandManager;

public class TerminalPanel extends Panel {
    private final TextBox inputBox;
    private final TextBox outputBox;
    private final CommandManager commandManager;

    public TerminalPanel(CommandManager commandManager) {
        super(new LinearLayout(com.googlecode.lanterna.gui2.Direction.VERTICAL));
        this.commandManager = commandManager;

        outputBox = new TextBox(new TerminalSize(100, 20), TextBox.Style.MULTI_LINE);
        outputBox.setReadOnly(true);
        addComponent(outputBox.withBorder(Borders.singleLine("Output")));

        inputBox = new TextBox(new TerminalSize(100, 15), TextBox.Style.MULTI_LINE);
        addComponent(inputBox.withBorder(Borders.singleLine("Input")));
    }

    public void addMessage(String message) {
        outputBox.setText(outputBox.getText() + message + "\n");
    }

    public String getInput() {
        return inputBox.getText();
    }

    public void clearInput() {
        inputBox.setText("");
    }

    public void processInput(String input) {
        commandManager.processInput(input);
    }
}
