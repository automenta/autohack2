package dumb.code.tools;

import dumb.common.tools.Tool;
import dumb.code.tools.vcs.Backend;
import dumb.code.tools.vcs.FileBackend;
import dumb.code.tools.vcs.GitBackend;
import dumb.code.tools.vcs.PijulBackend;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class VersionControlTool implements Tool {

    private Backend backend;

    public VersionControlTool(String rootDir) {
        autodetectBackend(rootDir);
    }

    private void autodetectBackend(String rootDir) {
        if (isCommandAvailable("pijul", "--version")) {
            setBackend("pijul", rootDir);
        } else if (isCommandAvailable("git", "--version")) {
            setBackend("git", rootDir);
        } else {
            setBackend("file", rootDir);
        }
    }

    private boolean isCommandAvailable(String... command) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            int exitCode = process.waitFor();
            return exitCode == 0;
        } catch (IOException | InterruptedException e) {
            return false;
        }
    }

    private void setBackend(String backendType, String rootDir) {
        switch (backendType.toLowerCase()) {
            case "file":
                this.backend = new FileBackend(rootDir);
                break;
            case "git":
                this.backend = new GitBackend();
                break;
            case "pijul":
                this.backend = new PijulBackend();
                break;
            default:
                throw new IllegalArgumentException("Unsupported backend: " + backendType);
        }
    }

    public String name() {
        return "vcs";
    }

    public String description() {
        return "A tool for interacting with the version control system.";
    }

    public String run(Map<String, Object> args) {
        String command = (String) args.get("command");
        if (command == null) {
            return "Error: command not specified.";
        }
        switch (command) {
            case "diff":
                return diff();
            case "status":
                return status();
            case "add":
                add((String) args.get("file"));
                return "File added.";
            case "revert":
                revert((String) args.get("file"));
                return "File reverted.";
            case "revertAll":
                revertAll();
                return "All changes reverted.";
            case "listTrackedFiles":
                return String.join("\n", listTrackedFiles());
            case "listUntrackedFiles":
                return String.join("\n", listUntrackedFiles());
            case "apply":
                apply((String) args.get("patch"));
                return "Patch applied.";
            case "record":
                record((String) args.get("message"));
                return "Changes recorded.";
            case "channel":
                channel((String) args.get("subcommand"), (String) args.get("name"));
                return "Channel command executed.";
            case "conflicts":
                return conflicts();
            default:
                return "Unknown command: " + command;
        }
    }

    public String diff() {
        return backend.diff().join();
    }

    public String status() {
        return backend.status().join();
    }

    public void add(String file) {
        backend.add(file).join();
    }

    public void revert(String file) {
        backend.revert(file).join();
    }

    public void revertAll() {
        backend.revertAll().join();
    }

    public List<String> listTrackedFiles() {
        return backend.listTrackedFiles().join();
    }

    public List<String> listUntrackedFiles() {
        return backend.listUntrackedFiles().join();
    }

    public void apply(String patch) {
        backend.apply(patch).join();
    }

    public void record(String message) {
        backend.record(message).join();
    }

    public void channel(String subcommand, String name) {
        if (backend instanceof PijulBackend pijulBackend) {
            try {
                pijulBackend.channel(subcommand, name).get();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            throw new UnsupportedOperationException("This backend does not support channels.");
        }
    }

    public String conflicts() {
        if (backend instanceof PijulBackend pijulBackend) {
            try {
                return pijulBackend.conflicts().join();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            throw new UnsupportedOperationException("This backend does not support conflicts.");
        }
    }
}
