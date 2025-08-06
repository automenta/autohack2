package dumb.tools.util;

public interface IProcessRunner {
    ProcessResult run(String... command);
    ProcessResult runWithInput(String input, String... command);
}
