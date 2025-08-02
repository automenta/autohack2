package dumb.code.tui;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SyntaxHighlightingPanel extends AbstractComponent<SyntaxHighlightingPanel> {

    private String text = "";
    private static final Set<String> JAVA_KEYWORDS = new HashSet<>(Arrays.asList(
            "abstract", "continue", "for", "new", "switch", "assert", "default", "goto", "package", "synchronized",
            "boolean", "do", "if", "private", "this", "break", "double", "implements", "protected", "throw",
            "byte", "else", "import", "public", "throws", "case", "enum", "instanceof", "return", "transient",
            "catch", "extends", "int", "short", "try", "char", "final", "interface", "static", "void",
            "class", "finally", "long", "strictfp", "volatile", "const", "float", "native", "super", "while"
    ));
    private static final Pattern KEYWORD_PATTERN = Pattern.compile("\\b(" + String.join("|", JAVA_KEYWORDS) + ")\\b");


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
                graphics.applyThemeStyle(getTheme().getDefinition(Panel.class).getNormal());
                String[] lines = component.getText().split("\n", -1);
                TextColor.ANSI normalColor = TextColor.ANSI.WHITE;
                if (getTheme() != null && getTheme().getDefinition(Panel.class).getNormal().getForeground() instanceof TextColor.ANSI) {
                    normalColor = (TextColor.ANSI) getTheme().getDefinition(Panel.class).getNormal().getForeground();
                }


                for (int i = 0; i < lines.length; i++) {
                    String line = lines[i];
                    Matcher matcher = KEYWORD_PATTERN.matcher(line);
                    int lastEnd = 0;
                    int column = 0;

                    while (matcher.find()) {
                        // Draw text before the keyword
                        String preMatch = line.substring(lastEnd, matcher.start());
                        graphics.putString(column, i, preMatch);
                        column += preMatch.length();

                        // Draw the keyword
                        String keyword = matcher.group(1);
                        graphics.setForegroundColor(TextColor.ANSI.BLUE);
                        graphics.enableModifiers(SGR.BOLD);
                        graphics.putString(column, i, keyword);
                        graphics.setForegroundColor(normalColor);
                        graphics.disableModifiers(SGR.BOLD);
                        column += keyword.length();

                        lastEnd = matcher.end();
                    }

                    // Draw the rest of the line
                    if (lastEnd < line.length()) {
                        graphics.putString(column, i, line.substring(lastEnd));
                    }
                }
            }
        };
    }
}
