package dumb.hack.tools;

import dumb.mcr.tools.Tool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

public class CommandExecutionTool implements Tool {

    @Override
    public String name() {
        return "commandExecution";
    }

    @Override
    public String description() {
        return "A tool for executing shell commands.";
    }

    @Override
    public String run(Map<String, Object> args) {
        String methodName = (String) args.get("method");
        if ("runCommand".equals(methodName)) {
            return runCommand((String) args.get("command"));
        } else {
            return "Error: Unknown method " + methodName;
        }
    }

    private String runCommand(String command) {
        try {
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            StringBuilder output = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            process.waitFor();
            return output.toString();
        } catch (IOException | InterruptedException e) {
            return "Error executing command: " + e.getMessage();
        }
    }
}
