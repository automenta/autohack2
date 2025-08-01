package dumb.hack;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigManager {
    private static final String CONFIG_DIR = System.getProperty("user.home") + File.separator + ".config" + File.separator + "autohack";
    private static final String CONFIG_FILE = "autohack.properties";
    private final Properties properties;

    public ConfigManager() {
        properties = new Properties();
        loadProperties();
    }

    private void loadProperties() {
        File configFile = new File(CONFIG_DIR, CONFIG_FILE);
        if (configFile.exists()) {
            try (FileInputStream in = new FileInputStream(configFile)) {
                properties.load(in);
            } catch (IOException e) {
                // For now, we'll just print the stack trace.
                // In a real application, you might want to use a logger.
                e.printStackTrace();
            }
        }
    }

    public void saveProperties() {
        File configDir = new File(CONFIG_DIR);
        if (!configDir.exists()) {
            configDir.mkdirs();
        }
        File configFile = new File(CONFIG_DIR, CONFIG_FILE);
        try (FileOutputStream out = new FileOutputStream(configFile)) {
            properties.store(out, "AutoHack Configuration");
        } catch (IOException e) {
            // For now, we'll just print the stack trace.
            // In a real application, you might want to use a logger.
            e.printStackTrace();
        }
    }

    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    public void setProperty(String key, String value) {
        properties.setProperty(key, value);
    }

    public Properties getProperties() {
        return properties;
    }
}
