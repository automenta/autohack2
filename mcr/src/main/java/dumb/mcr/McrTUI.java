package dumb.mcr;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

public class McrTUI {
    private final Session session;

    public McrTUI(Session session) {
        this.session = session;
    }

    public void start() throws IOException {
        System.out.println("MCR Interactive TUI");
        System.out.println("Type 'exit' to quit.");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            while (true) {
                System.out.print("> ");
                String line = reader.readLine();
                if (line == null || line.equalsIgnoreCase("exit")) {
                    break;
                }
                if (line.isBlank()) {
                    continue;
                }

                QueryResult result = session.nquery(line);

                System.out.println("Original Query: " + result.originalQuery());
                if (result.success()) {
                    System.out.println("Success!");
                    if (result.bindings() != null && !result.bindings().isEmpty()) {
                        System.out.println("Solutions:");
                        result.getBindings().forEach(solution -> System.out.println("  " + solution));
                    } else {
                        System.out.println("Query was successful, but returned no solutions.");
                    }
                } else {
                    System.err.println("MCR query failed.");
                }
            }
        }
    }
}
