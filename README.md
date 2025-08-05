# Project Structure

This project is a multi-module Maven project. It consists of four modules:

- `common`: A shared module containing common functionality.
- `code`: A headless library that provides the core logic for code analysis, manipulation, and AI interaction.
- `hack`: The main "AutoHack" application, providing a terminal-based UI for the AI pair programming tool.
- `mcr`: The "Model Context Reasoner" application, a neurosymbolic reasoning core.

This structure separates the core logic (`code`) from the user interface (`hack`), promoting modularity and reusability.

## Vision: An Autonomous Software Engineering Agent

The project, with its `hack` and `mcr` modules, has the potential to become a fully autonomous AI software engineer.
This agent would be capable of not just assisting with coding tasks, but independently managing and evolving a codebase
to meet high-level goals.

The combination of `hack` and `mcr` creates a powerful synergy. `mcr` provides the high-level reasoning and
planning, while `hack` provides the tools and user interface to execute those plans in the real world of code, files, and development tools. This separation of concerns mirrors the way a human software architect and a developer might work together.

This vision represents a significant leap forward from current AI coding assistants, moving towards a future where AI
can be a true partner in the creative and complex process of software development.

## Building the Project

To build the project, run the following command from the root directory:

```bash
mvn clean install
```

This will build all four modules and install them into your local Maven repository.

## Running the Application

To run the application, execute the following command from the root directory:

```bash
java -jar hack/target/hack-1.0-SNAPSHOT.jar
```

This will start the unified AutoHack terminal UI, which integrates the `code`, `mcr`, and `common` modules.
