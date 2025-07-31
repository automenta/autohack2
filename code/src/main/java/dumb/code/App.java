package dumb.code;

import java.io.IOException;

public class App {
    public static void main(String[] args) {
        try {
            // Initialize container for dependency injection
            Context context = new Context(args);

            // Create and start the main application
            PijulAider aider = new PijulAider(context);
            aider.start();

        } catch (IOException e) {
            System.err.println("Error initializing application: " + e.getMessage());
            e.printStackTrace(); // Also print stack trace for debugging
            System.exit(1);
        }
    }
}