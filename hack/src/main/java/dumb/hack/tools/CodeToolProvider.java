package dumb.hack.tools;

import dumb.code.tools.CodebaseTool;
import dumb.code.tools.FileSystemTool;
import dumb.common.tools.Tool;
import dumb.mcr.tools.ToolProvider;

import java.util.HashMap;
import java.util.Map;

public class CodeToolProvider implements ToolProvider {

    private final Map<String, Tool> tools = new HashMap<>();

    public CodeToolProvider(FileSystemTool fileSystemTool, CodebaseTool codebaseTool) {
        CodeModificationTool modifyFileTool = new CodeModificationTool(fileSystemTool, codebaseTool);
        tools.put(modifyFileTool.name(), modifyFileTool);
    }

    @Override
    public Map<String, Tool> getTools() {
        return tools;
    }
}
