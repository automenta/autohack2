# Project Structure

This project is a multi-module Maven project. It consists of three modules:

- `common`: A shared module containing common functionality, including the LangChain4j LM interface.
- `code`: The "AutoHack" application, a terminal-based AI pair programming tool.
- `mcr`: The "Model Context Reasoner" application, a neurosymbolic reasoning core.

## Building the Project

To build the project, run the following command from the root directory:

```bash
mvn clean install
```

This will build all three modules and install them into your local Maven repository.

## Running the Applications

### AutoHack

To run the AutoHack application, execute the following command:

```bash
java -jar code/target/code-1.0-SNAPSHOT.jar
```

### MCR

To run the MCR application, execute the following command:

```bash
java -jar mcr/target/mcr-1.0.jar
```
