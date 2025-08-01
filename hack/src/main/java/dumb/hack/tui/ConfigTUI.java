package dumb.hack.tui;

import com.googlecode.lanterna.gui2.Panel;
import dumb.hack.ConfigManager;

public class ConfigTUI {
    private final ConfigManager configManager;

    public ConfigTUI(ConfigManager configManager) {
        this.configManager = configManager;
    }

    public Panel createPanel() {
        return new ConfigPanel(configManager);
    }
}
