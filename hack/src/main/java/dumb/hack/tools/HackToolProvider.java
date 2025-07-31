package dumb.hack.tools;

import dumb.mcr.tools.Tool;
import dumb.mcr.tools.ToolProvider;

import java.util.HashMap;
import java.util.Map;

public class HackToolProvider implements ToolProvider {
    private final Map<String, Tool> tools = new HashMap<>();

    public HackToolProvider() {
        registerTool(new FileSystemTool());
        registerTool(new CommandExecutionTool());
    }

    private void registerTool(Tool tool) {
        tools.put(tool.name(), tool);
    }

    @Override
    public Map<String, Tool> getTools() {
        return tools;
    }
}
