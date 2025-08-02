package dumb.code.commands.reason;

import dumb.code.Code;
import dumb.code.commands.Command;
import dumb.code.help.HelpService;
import dumb.code.tui.events.*;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Collectors;

public class ReasonCommand implements Command {

    private final Code code;
    private final HelpService helpService;

    public ReasonCommand(Code code, HelpService helpService) {
        this.code = code;
        this.helpService = helpService;
    }

    @Override
    public void execute(String[] args) {
        BlockingQueue<UIEvent> eventQueue = code.getEventQueue();

        String task = String.join(" ", args);
        postEvent(eventQueue, new StatusUpdateEvent("Reasoning about task: " + task));

        String prompt = "You are an expert software engineer. Your task is to break down the following natural language request into a series of commands to be executed in a terminal. The available commands are: /ls, /add, /rm, /create, /edit, /run, /test. Please provide only the commands, each on a new line.\n\nTask: " + task;

        String response = code.lmManager.generateResponse(prompt);

        if (eventQueue == null) {
            System.out.println("LLM Response:");
            System.out.println(response);
        }

        List<String> commands = Arrays.stream(response.split("\n"))
                .filter(s -> !s.trim().isEmpty())
                .collect(Collectors.toList());

        postEvent(eventQueue, new PlanGeneratedEvent(commands));

        boolean allSucceeded = true;
        for (int i = 0; i < commands.size(); i++) {
            String command = commands.get(i);
            postEvent(eventQueue, new CommandStartEvent(i));

            if (eventQueue == null) {
                System.out.println("Executing command: " + command);
            }

            code.commandManager.processInput(command);

            // TODO: This is a temporary simplification. A proper implementation requires
            // the CommandManager to return a status. For now, we assume success to
            // drive the UI feedback loop.
            boolean success = true;
            postEvent(eventQueue, new CommandFinishEvent(i, success));

            if (!success) {
                allSucceeded = false;
                break; // Stop execution on first failure
            }
        }

        postEvent(eventQueue, new TaskFinishEvent(allSucceeded));
    }

    private void postEvent(BlockingQueue<UIEvent> queue, UIEvent event) {
        if (queue != null) {
            try {
                queue.put(event);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Failed to post UI event: " + e.getMessage());
            }
        }
    }
}
