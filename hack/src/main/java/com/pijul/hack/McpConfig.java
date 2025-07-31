package com.pijul.hack;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class McpConfig {

    private String jsonConfig;

    public McpConfig() {
        // Default configuration
        this.jsonConfig = "{\"type\":\"stdio\",\"command\":\"\"}";
    }

    public String getJsonConfig() {
        return jsonConfig;
    }

    public void setJsonConfig(String jsonConfig) {
        this.jsonConfig = jsonConfig;
    }

    @Override
    public String toString() {
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            JsonElement el = JsonParser.parseString(jsonConfig);
            return gson.toJson(el);
        } catch (Exception e) {
            return "Invalid JSON config: " + jsonConfig;
        }
    }
}
