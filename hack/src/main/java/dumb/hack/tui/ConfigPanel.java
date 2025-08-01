package dumb.hack.tui;

import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.Label;
import com.googlecode.lanterna.gui2.LinearLayout;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.TextBox;
import dumb.hack.ConfigManager;

import java.util.Properties;

public class ConfigPanel extends Panel {
    private final ConfigManager configManager;
    private final TextBox providerTextBox;
    private final TextBox modelTextBox;
    private final TextBox apiKeyTextBox;

    public ConfigPanel(ConfigManager configManager) {
        super(new LinearLayout(com.googlecode.lanterna.gui2.Direction.VERTICAL));
        this.configManager = configManager;

        Properties props = configManager.getProperties();

        addComponent(new Label("Provider:"));
        providerTextBox = new TextBox(props.getProperty("lm.provider", ""));
        addComponent(providerTextBox);

        addComponent(new Label("Model:"));
        modelTextBox = new TextBox(props.getProperty("lm.model", ""));
        addComponent(modelTextBox);

        addComponent(new Label("API Key:"));
        apiKeyTextBox = new TextBox(props.getProperty("lm.apiKey", "")).setMask('*');
        addComponent(apiKeyTextBox);

        addComponent(new Button("Save", this::saveConfig));
    }

    private void saveConfig() {
        configManager.setProperty("lm.provider", providerTextBox.getText());
        configManager.setProperty("lm.model", modelTextBox.getText());
        configManager.setProperty("lm.apiKey", apiKeyTextBox.getText());
        configManager.saveProperties();
    }
}
