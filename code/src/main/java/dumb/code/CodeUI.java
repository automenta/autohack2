package dumb.code;

import com.googlecode.lanterna.gui2.Panel;
import dumb.code.tui.Terminal;
import dumb.code.tui.IBreadcrumbManager;

public class CodeUI {
    private final Code code;
    private final IBreadcrumbManager breadcrumbManager;
    private Terminal tui;

    public CodeUI(Code code, IBreadcrumbManager breadcrumbManager) {
        this.code = code;
        this.breadcrumbManager = breadcrumbManager;
    }

    public Panel createPanel() {
        this.tui = new Terminal(code.commandManager, breadcrumbManager);
        code.setTerminal(tui); // Set the terminal in the container
        return tui.getTerminalPanel();
    }

    public Terminal getTerminal() {
        return tui;
    }

    public void stop() {
        if (tui != null) {
            // It's good practice to have a way to gracefully shut down the TUI
            // For now, we can just stop the command manager's listening aspect
            code.commandManager.stopListening();
        }
    }
}
