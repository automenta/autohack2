package dumb.hack.ui;

import com.googlecode.lanterna.gui2.Panel;
import dumb.code.Code;
import dumb.hack.ui.tui.Terminal;

public class CodeUI {
    private final Code code;
    private Terminal terminal;

    public CodeUI(Code code) {
        this.code = code;
    }

    public Panel createPanel() {
        this.terminal = new Terminal(this.code);
        return terminal.getPanel();
    }
}
