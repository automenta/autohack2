package dumb.code.agent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A registry for discovering and managing tools that the agent can use.
 */
public class ToolRegistry {

    private final Map<String, Object> tools = new HashMap<>();

    public void register(Object tool) {
        // Use simple class name as the tool name for now
        String toolName = tool.getClass().getSimpleName();
        if (tools.containsKey(toolName)) {
            throw new IllegalArgumentException("Tool with name '" + toolName + "' is already registered.");
        }
        tools.put(toolName, tool);
    }

    public Object getTool(String name) {
        return tools.get(name);
    }

    public List<Object> getAllTools() {
        return new ArrayList<>(tools.values());
    }

    /**
     * Placeholder for future logic to automatically discover tools
     * from the classpath using annotations.
     */
    public void discoverTools() {
        // TODO: Implement classpath scanning for @Tool annotated classes
    }
}
