package dumb.hack.tools;

import dumb.code.CodebaseManager;
import dumb.code.FileManager;
import dumb.mcr.tools.Tool;
import dumb.mcr.tools.ToolProvider;

import java.util.HashMap;
import java.util.Map;

public class CodeToolProvider implements ToolProvider {

    private final Map<String, Tool> tools = new HashMap<>();

    public CodeToolProvider(FileManager fileManager, CodebaseManager codebaseManager) {
        CodeModificationTool modifyFileTool = new CodeModificationTool(fileManager, codebaseManager);
        tools.put(modifyFileTool.name(), modifyFileTool);
    }

    @Override
    public Map<String, Tool> getTools() {
        return tools;
    }
}
