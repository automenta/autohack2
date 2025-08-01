package dumb.code.util;

public interface IProcessRunner {
    ProcessResult run(String... command);
    ProcessResult runWithInput(String input, String... command);
}
