package dumb.code.commands.reason;

import dumb.code.Code;
import dumb.code.commands.Command;
import dumb.code.help.HelpService;

public class ReasonCommand implements Command {

    private final Code code;
    private final HelpService helpService;

    public ReasonCommand(Code code, HelpService helpService) {
        this.code = code;
        this.helpService = helpService;
    }

    @Override
    public void execute(String[] args) {
        String task = String.join(" ", args);
        System.out.println("Reasoning about task: " + task);

        String prompt = "You are an expert software engineer. Your task is to break down the following natural language request into a series of commands to be executed in a terminal. The available commands are: /ls, /add, /rm, /create, /edit, /run, /test. Please provide only the commands, each on a new line.\n\nTask: " + task;

        String response = code.lmManager.generateResponse(prompt);

        System.out.println("LLM Response:");
        System.out.println(response);

        String[] commands = response.split("\n");
        for (String command : commands) {
            if (command.trim().isEmpty()) {
                continue;
            }
            System.out.println("Executing command: " + command);
            code.commandManager.processInput(command);
        }
    }
}
