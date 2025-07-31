# AutoHack

AutoHack is an Aider clone: a terminal-based AI pair programming tool for LLM-driven code editing, feature generation, bug fixing, and change management. It supports three versioning backends—plain files (no VCS), Git, or Pijul—with Pijul as the preferred option for its commutative patches, enabling flexible, non-linear workflows ideal for advanced LLM experiments. Users can start with files or Git and upgrade to Pijul seamlessly for enhanced features.

#### Key Enhancements in Revision
- **Multi-Backend Support**: Configurable via CLI flags (e.g., `--backend=pijul`). Defaults to Pijul if installed; falls back to Git or files otherwise. Upgrading: Tool detects current backend and offers migration commands (e.g., initialize Pijul repo from Git via existing converters like PijulGit).
- **Preference for Pijul**: Prompts users to switch to Pijul during setup, highlighting benefits like conflict-free merging, granular patch unapplying, and composable changes for AI iterations. For upgrades, provide guided steps: Export Git history to Pijul, or layer Pijul atop files/Git for hybrid use.
- **Backend-Agnostic Core**: Core logic (chat interface, LLM integration, codebase mapping, testing) remains unchanged; versioning abstracted to a modular layer handling init, add, record/commit, apply/revert, and diff/preview.

#### Architecture Overview
1. **User Interface**: CLI chat (e.g., `pijulaider --backend=pijul file.py --model=gpt-4o`). Supports voice, images, in-file edits.
2. **LLM Layer**: API calls to models; outputs edits as diffs. For Pijul, prompt for modular, patch-like changes.
3. **Codebase Management**: Context-aware file handling; partial loads for efficiency.
4. **Versioning Layer**: 
   - **Files**: Simple save/backup (e.g., timestamped copies for undo).
   - **Git**: Linear commits, branches for experiments (compatible with Aider).
   - **Pijul**: Patch recording, channels for parallels, apply/unapply for flexibility.
5. **Validation**: Auto-lint/test; LLM fixes as new changes.
6. **Extensibility**: Plugins for backend switches; logs for migration.

#### Workflow
1. **Setup**: Choose backend (prefer Pijul prompt). Init repo if needed; upgrade option from files/Git.
2. **Edit Cycle**: User query → LLM edit → Preview/apply locally → Record (patch/commit/save).
3. **Advanced Features (Pijul-Preferred)**: Parallel patches/channels for AI variants; commutative applies; conflict patches. Fallbacks: Git branches for experiments, file copies for basics.
4. **Migration/Upgrade**: Run `--upgrade-to-pijul`: Converts Git/files to Pijul repo, preserving history where possible.
5. **Exit**: Changes persisted per backend; easy rollback (unapply/revert/restore).

#### Advantages
- **Flexibility**: Start simple (files), scale to Git, upgrade to Pijul for AI-suited non-linearity.
- **Actionable Adoption**: Install Pijul CLI; tool handles rest. Enables exploratory LLM dev without VCS overhead initially, but encourages Pijul for power users.
- **Tradeoffs**: Files lack history; Git linear; Pijul advanced but requires learning—tool includes onboarding tips.

This design keeps AutoHack versatile while prioritizing Pijul's strengths for superior LLM-driven development. Prototype via modular abstraction for easy backend swaps.


----

# AutoHack

Terminal-based AI pair programming tool designed to streamline your workflow with LLM-driven code editing, feature generation, bug fixing, and change management. It supports Git, Pijul, and a simple file-based versioning system.

## Features

- **LLM-driven code editing:**  Leverage the power of language models to generate code, fix bugs, and refactor your codebase.
- **Seamless version control:**  AutoHack integrates with Git and Pijul, allowing you to easily track changes and manage your repository.
- **Extensible command system:**  AutoHack comes with a set of built-in commands, and you can easily add your own to extend its functionality.
- **Terminal-based UI:** The user-friendly terminal interface provides a smooth and efficient user experience.

## Installation

1.  Clone the repository:
    ```bash
    git clone https://github.com/automenta/pijulaider.git
    ```
2.  Install the dependencies:
    ```bash
    npm install
    ```
3.  Run the tool:
    ```bash
    ./src/index.js
    ```

## Usage

AutoHack supports a variety of commands to help you with your coding tasks. Here are a few examples:

-   `/add <file>`: Add a file to the chat so the LLM can see it.
-   `/diff`: Show the current changes.
-   `/record <message>`: Record the current changes with a message.
-   `/help`: Show a list of available commands.

For a full list of commands, use the `/help` command within the tool.
