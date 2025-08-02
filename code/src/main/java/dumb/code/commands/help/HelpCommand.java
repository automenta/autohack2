package dumb.code.commands.help;

import dumb.code.MessageHandler;
import dumb.code.commands.Command;
import dumb.code.help.HelpService;

import java.util.List;

public class HelpCommand implements Command {

    private final HelpService helpService;
    private final MessageHandler messageHandler;

    public HelpCommand(HelpService helpService, MessageHandler messageHandler) {
        this.helpService = helpService;
        this.messageHandler = messageHandler;
    }

    @Override
    public void init() {
        // No initialization needed for HelpCommand
    }

    @Override
    public void execute(String[] args) {
        List<String> helpMessages;
        if (args.length == 0) {
            helpMessages = helpService.getHelp();
        } else {
            helpMessages = helpService.getHelp(args[0]);
        }

        for (String message : helpMessages) {
            messageHandler.addMessage("system", message);
        }
    }

    @Override
    public void cleanup() {
        // No cleanup needed for HelpCommand
    }
}