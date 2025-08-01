package dumb.code.tui;

import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.LinearLayout;
import com.googlecode.lanterna.gui2.TextBox;
import com.googlecode.lanterna.gui2.Label;
import com.googlecode.lanterna.TerminalSize;
import dumb.code.CommandManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TerminalPanel extends Panel {
    private final List<String> messages = Collections.synchronizedList(new ArrayList<>());
    private final TextBox inputBox;
    private final Panel messagesPanel;
    private final CommandManager commandManager;

    public TerminalPanel(CommandManager commandManager) {
        super(new LinearLayout(com.googlecode.lanterna.gui2.Direction.VERTICAL));
        this.commandManager = commandManager;

        messagesPanel = new Panel(new LinearLayout(com.googlecode.lanterna.gui2.Direction.VERTICAL));
        addComponent(messagesPanel.withBorder(com.googlecode.lanterna.gui2.Borders.singleLine("Output")));

        inputBox = new TextBox(new TerminalSize(100, 5));
        addComponent(inputBox.withBorder(com.googlecode.lanterna.gui2.Borders.singleLine("Input")));
    }

    public void addMessage(String message) {
        messages.add(message);
        messagesPanel.removeAllComponents();
        for (String msg : messages) {
            messagesPanel.addComponent(new Label(msg));
        }
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
