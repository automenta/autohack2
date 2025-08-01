package dumb.code.tui;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;

public class SyntaxHighlightingPanel extends AbstractComponent<SyntaxHighlightingPanel> {

    private String text = "";

    public SyntaxHighlightingPanel(String text) {
        setText(text);
    }

    public SyntaxHighlightingPanel() {
        this("");
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
        invalidate();
    }

    @Override
    protected TerminalSize calculatePreferredSize() {
        String[] lines = text.split("\n", -1);
        int width = 0;
        for (String line : lines) {
            if (line.length() > width) {
                width = line.length();
            }
        }
        return new TerminalSize(width, lines.length);
    }

    @Override
    public ComponentRenderer<SyntaxHighlightingPanel> createDefaultRenderer() {
        return new ComponentRenderer<SyntaxHighlightingPanel>() {
            @Override
            public TerminalSize getPreferredSize(SyntaxHighlightingPanel component) {
                return component.calculatePreferredSize();
            }

            @Override
            public void drawComponent(TextGUIGraphics graphics, SyntaxHighlightingPanel component) {
                graphics.applyThemeStyle(component.getThemeDefinition().getNormal());
                String[] lines = component.getText().split("\n", -1);
                for (int i = 0; i < lines.length; i++) {
                    graphics.putString(0, i, lines[i]);
                }
            }
        };
    }
}
