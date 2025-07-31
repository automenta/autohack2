package com.pijul.aider.commands;

import com.pijul.aider.PijulAider;

public class ExitCommand implements Command {
    private final PijulAider aider;

    public ExitCommand(PijulAider aider) {
        this.aider = aider;
    }

    @Override
    public void execute(String[] args) {
        aider.stop();
        System.exit(0);
    }
}
