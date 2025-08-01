package dumb.code.tui;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.Borders;
import com.googlecode.lanterna.gui2.Direction;
import com.googlecode.lanterna.gui2.LinearLayout;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.TextBox;
import dumb.code.CommandManager;

public class TerminalPanel extends Panel {
    private final TextBox inputBox;
    private final SyntaxHighlightingPanel outputBox;
    private final CommandManager commandManager;
    private final IBreadcrumbManager breadcrumbManager;

    public TerminalPanel(CommandManager commandManager, IBreadcrumbManager breadcrumbManager) {
        super(new LinearLayout(com.googlecode.lanterna.gui2.Direction.HORIZONTAL));
        this.commandManager = commandManager;
        this.breadcrumbManager = breadcrumbManager;

        // Split view for LLM response vs code/input
        outputBox = new SyntaxHighlightingPanel();
        Panel outputPanel = new Panel(new LinearLayout(Direction.VERTICAL));
        outputPanel.addComponent(outputBox);
        addComponent(outputPanel.withBorder(Borders.singleLine("LLM Response")));


        inputBox = new TextBox(new TerminalSize(50, 35), TextBox.Style.MULTI_LINE);
        addComponent(inputBox.withBorder(Borders.singleLine("Code / Input")));
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
