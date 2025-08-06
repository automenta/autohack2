package dumb.hack.tools;

import dumb.tools.IFileManager;
import dumb.tools.ListFilesTool;
import dumb.tools.ReadFileTool;
import dumb.tools.WriteFileTool;
import dumb.tools.Workspace;
import dumb.common.tools.Tool;
import dumb.common.tools.ToolProvider;

import java.util.HashMap;
import java.util.Map;

public class CodeToolProvider implements ToolProvider {

    private final Map<String, Tool> tools = new HashMap<>();

    public CodeToolProvider(IFileManager fileManager, Workspace workspace) {
        CodeModificationTool modifyFileTool = new CodeModificationTool(fileManager, workspace);
        tools.put(modifyFileTool.name(), modifyFileTool);

        ReadFileTool readFileTool = new ReadFileTool(fileManager);
        tools.put(readFileTool.name(), readFileTool);

        WriteFileTool writeFileTool = new WriteFileTool(fileManager);
        tools.put(writeFileTool.name(), writeFileTool);

        ListFilesTool listFilesTool = new ListFilesTool(fileManager);
        tools.put(listFilesTool.name(), listFilesTool);
    }

    @Override
    public Map<String, Tool> getTools() {
        return tools;
    }
}
