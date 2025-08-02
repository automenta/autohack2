package dumb.code.help;

import dumb.code.Code;
import dumb.code.UIManager;

public class HelpManager {

    private UIManager uiManager;
    private AbstractHelp currentHelp;
    private boolean isHelpActive = false;

    public HelpManager(Code code) {
        // Dependencies are injected via setters to avoid circular dependencies.
    }

    public void setUIManager(UIManager uiManager) {
        this.uiManager = uiManager;
    }

    public void startHelp(AbstractHelp help) {
        this.currentHelp = help;
        this.isHelpActive = true;
        if (uiManager != null) {
            uiManager.displayMessage("💡 " + help.getWelcomeMessage());
            uiManager.displayHelpMessage();
        }
    }

    public void stopHelp() {
        this.isHelpActive = false;
        this.currentHelp = null;
    }

    public boolean isHelpActive() {
        return isHelpActive;
    }

    public void processInput(String input) {
        if (isHelpActive && currentHelp != null) {
            currentHelp.processInput(input);
            if (uiManager != null) {
                uiManager.displayHelpMessage();
            }
        }
    }

    public String getCurrentHelpMessage() {
        if (isHelpActive && currentHelp != null) {
            return currentHelp.getCurrentStepMessage();
        }
        return "";
    }
}
