package dumb.mcr.tools;

import dumb.common.tools.Tool;
import dumb.mcr.Session;

import java.util.HashMap;
import java.util.Map;

public class DefaultToolProvider implements ToolProvider {

    private final Map<String, Tool> tools = new HashMap<>();

    public DefaultToolProvider() {
    }

    public void setSession(Session session) {
        tools.put("java_parser", new JavaParserTool(session));
    }

    @Override
    public Map<String, Tool> getTools() {
        return tools;
    }
}
