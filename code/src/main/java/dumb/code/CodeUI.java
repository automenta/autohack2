package dumb.code;

import com.googlecode.lanterna.gui2.Panel;
import dumb.code.agent.AgentOrchestrator;
import dumb.code.tui.Terminal;

public class CodeUI {
    private final AgentOrchestrator orchestrator;
    private Terminal tui;

    public CodeUI(AgentOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    public Panel createPanel() {
        this.tui = new Terminal(orchestrator.getCommandManager());
        // The terminal doesn't need to be set on a container anymore
        return tui.getTerminalPanel();
    }

    public Terminal getTerminal() {
        return tui;
    }

    public void stop() {
        if (tui != null) {
            // It's good practice to have a way to gracefully shut down the TUI
            // For now, we can just stop the command manager's listening aspect
            orchestrator.getCommandManager().stopListening();
        }
    }
}
