package dumb.code.help;

import java.util.List;

/**
 * A service for providing help and tutorials to the user.
 */
public interface HelpService {

    /**
     * Gets a general help message, typically listing available commands.
     *
     * @return A list of help messages.
     */
    List<String> getHelp();

    /**
     * Gets help for a specific command.
     *
     * @param commandName The name of the command.
     * @return A list of help messages for the specified command.
     */
    List<String> getHelp(String commandName);

    /**
     * Starts the tutorial.
     */
    void startTutorial();

    /**
     * Notifies the help service that a command has been executed.
     *
     * @param command The command that was executed, including arguments.
     */
    void onCommandExecuted(String[] command);
}
