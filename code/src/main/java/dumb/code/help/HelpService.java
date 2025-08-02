package dumb.code.help;

import dumb.code.Code;
import dumb.code.project.ProjectTemplate;

import java.util.List;

/**
 * A service for providing help and tutorials to the user.
 */
public interface HelpService {

    /**
     * Sets the Code instance, used to access application state.
     * @param code The Code instance.
     */
    void setCode(Code code);

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
     * Gets a list of available project templates.
     * @return A list of project templates.
     */
    List<ProjectTemplate> getAvailableTemplates();

    /**
     * Starts the tutorial for a given project template.
     * @param template The project template to use for the tutorial.
     */
    void startTutorial(ProjectTemplate template);

    /**
     * Creates a project from a template.
     * @param template The template to create the project from.
     * @param targetDir The directory to create the project in.
     * @throws java.io.IOException if there is an error creating the project.
     */
    void createProject(ProjectTemplate template, java.io.File targetDir) throws java.io.IOException;

    /**
     * Stops the currently active tutorial.
     */
    void stopTutorial();

    /**
     * Notifies the help service that a command has been executed.
     *
     * @param command The command that was executed, including arguments.
     */
    void onCommandExecuted(String[] command);
}
