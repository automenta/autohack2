package dumb.mcr.tools;

import dumb.common.tools.Tool;
import java.util.Map;

public interface ToolProvider {
    Map<String, Tool> getTools();
}
