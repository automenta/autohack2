package com.pijul.hack.tools;

import com.pijul.mcr.tools.Tool;
import com.pijul.mcr.tools.ToolProvider;

import java.util.HashMap;
import java.util.Map;

public class HackToolProvider implements ToolProvider {
    private final Map<String, Tool> tools = new HashMap<>();

    public HackToolProvider() {
        registerTool(new FileSystemTool());
    }

    private void registerTool(Tool tool) {
        tools.put(tool.getName(), tool);
    }

    @Override
    public Map<String, Tool> getTools() {
        return tools;
    }
}
