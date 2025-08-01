package dumb.hack.tui;

import com.googlecode.lanterna.gui2.Panel;
import dumb.hack.App;

public class ConfigTUIComponent implements TUIComponent {
    @Override
    public String getName() {
        return "Config";
    }

    @Override
    public Panel createPanel(App app) {
        ConfigTUI configTUI = new ConfigTUI(app.getConfigManager());
        return configTUI.createPanel();
    }
}
