package dumb.tools;

import dumb.tools.versioning.Backend;
import dumb.tools.versioning.FileBackend;
import dumb.tools.versioning.GitBackend;
import dumb.tools.versioning.PijulBackend;

import java.io.IOException;

public class BackendManager {
    private final ToolContext toolContext;
    private Backend backend;
    private final IFileManager fileManager;

    public BackendManager(ToolContext toolContext, IFileManager fileManager) {
        this.toolContext = toolContext;
        this.fileManager = fileManager;
        // The backend is now initialized by the Container,
        // which calls either setBackend or autodetectBackend.
        // We can leave the field null here initially.
        this.backend = null;
    }

    public void autodetectBackend() {
        if (isCommandAvailable("pijul", "--version")) {
            setBackend("pijul");
        } else if (isCommandAvailable("git", "--version")) {
            setBackend("git");
        } else {
            setBackend("file");
        }
    }

    private boolean isCommandAvailable(String... command) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            int exitToolContext = process.waitFor();
            return exitToolContext == 0;
        } catch (IOException | InterruptedException e) {
            return false;
        }
    }

    public Backend getBackend() {
        if (backend == null) {
            // Fallback in case it was never initialized.
            autodetectBackend();
        }
        return backend;
    }

    public void setBackend(String backendType) {
        if (fileManager instanceof InMemoryFileManager) {
            this.backend = new dumb.tools.versioning.InMemoryBackend(fileManager);
            return;
        }

        switch (backendType.toLowerCase()) {
            case "file":
                this.backend = new FileBackend(fileManager.getRootDir());
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

    public void initialize() {
        // Initialization logic if needed
    }

    public void shutdown() {
        // Cleanup logic if needed
    }
}