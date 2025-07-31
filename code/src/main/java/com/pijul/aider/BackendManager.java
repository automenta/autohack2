package com.pijul.aider;

import com.pijul.aider.Backend;
import com.pijul.aider.versioning.FileBackend;
import com.pijul.aider.versioning.GitBackend;
import com.pijul.aider.versioning.PijulBackend;

public class BackendManager {
    private Backend backend;

    public BackendManager() {
        // Default to FileBackend for now
        this.backend = new FileBackend();
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

    public Backend getBackend() {
        return backend;
    }

    public void initialize() {
        // Initialization logic if needed
    }

    public void shutdown() {
        // Cleanup logic if needed
    }
}