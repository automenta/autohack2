package dumb.code;

import dumb.code.versioning.FileBackend;
import dumb.code.versioning.GitBackend;

import java.io.IOException;

public class BackendManager {
    private final Context context;
    private Backend backend;

    public BackendManager(Context context) {
        this.context = context;
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
            int exitCode = process.waitFor();
            return exitCode == 0;
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
        switch (backendType.toLowerCase()) {
            case "file":
                this.backend = new FileBackend();
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