package dumb.hack.tui.components;

import com.googlecode.lanterna.gui2.Borders;
import com.googlecode.lanterna.gui2.Direction;
import com.googlecode.lanterna.gui2.LinearLayout;
import com.googlecode.lanterna.gui2.Panel;
import dumb.code.Code;
import dumb.mcr.MCR;
import dumb.mcr.Session;

public class UnifiedPanel extends Panel {

    private final McrPanel mcrPanel;
    private final CodePanel codePanel;

    public UnifiedPanel(Code code, MCR mcr) {
        super(new LinearLayout(Direction.HORIZONTAL));

        // Create the child panels
        Session mcrSession = mcr.createSession();
        this.mcrPanel = new McrPanel(mcrSession);
        this.codePanel = new CodePanel(code);

        // Initially, only the MCR panel is visible (Progressive Disclosure)
        this.addComponent(mcrPanel.withBorder(Borders.singleLine("MCR (Reasoning)")));

        // Set up the communication listener from MCR to Code
        this.mcrPanel.setListener(resultText -> {
            // When a result is actioned in McrPanel...

            // 1. Pass the text to the code panel's input box.
            codePanel.setInputText(resultText);

            // 2. If the code panel isn't visible, add it to the UI.
            if (!this.getChildren().contains(codePanel)) {
                // This reveals the code panel on the right.
                this.addComponent(codePanel.withBorder(Borders.singleLine("Code (Action)")));
            }
        });
    }

    /**
     * Closes the resources used by the child panels.
     */
    public void close() {
        mcrPanel.close();
        codePanel.close();
    }
}
