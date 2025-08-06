package dumb.tools.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;

public class ProcessRunner implements IProcessRunner {

    @Override
    public ProcessResult runWithInput(String input, String... command) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()))) {
                writer.write(input);
                writer.newLine();
            }

            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }

            int exitToolContext = process.waitFor();
            return new ProcessResult(exitToolContext, output.toString());

        } catch (IOException | InterruptedException e) {
            return new ProcessResult(-1, e.getMessage());
        }
    }

    @Override
    public ProcessResult run(String... command) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }

            int exitToolContext = process.waitFor();
            return new ProcessResult(exitToolContext, output.toString());

        } catch (IOException | InterruptedException e) {
            return new ProcessResult(-1, e.getMessage());
        }
    }
}
