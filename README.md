# Project Structure

This project is a multi-module Maven project. It consists of three modules:

- `common`: A shared module containing common functionality, including the LangChain4j LM interface.
- `code`: The "AutoHack" application, a terminal-based AI pair programming tool.
- `mcr`: The "Model Context Reasoner" application, a neurosymbolic reasoning core.

## Vision: An Autonomous Software Engineering Agent

The project, with its `autohack` and `mcr` modules, has the potential to become a fully autonomous AI software engineer.
This agent would be capable of not just assisting with coding tasks, but independently managing and evolving a codebase
to meet high-level goals.

The combination of `autohack` and `mcr` creates a powerful synergy. `mcr` provides the high-level reasoning and
planning, while `autohack` executes those plans in the real world of code, files, and development tools. This separation
of concerns mirrors the way a human software architect and a developer might work together.

This vision represents a significant leap forward from current AI coding assistants, moving towards a future where AI
can be a true partner in the creative and complex process of software development.

## Building the Project

To build the project, run the following command from the root directory:

```bash
mvn clean install
```

This will build all three modules and install them into your local Maven repository.

## Running the Application

To run the application, execute the following command from the root directory:

```bash
java -jar hack/target/hack-1.0-SNAPSHOT.jar
```

This will start the AutoHack terminal UI, with the MCR reasoning engine integrated.
