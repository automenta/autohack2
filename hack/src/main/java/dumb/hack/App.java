package dumb.hack;

import java.io.IOException;

public class App {
    public static void main(String[] args) {
        if (args.length > 0 && "--execute".equals(args[0])) {
            if (args.length < 2) {
                System.err.println("Usage: --execute <command>");
                System.exit(1);
            }
            String command = args[1];
            try {
                HackContext context = new HackContext();
                context.setMessageHandler(System.out::println);
                context.init();
                context.getCommands().processInput(command);
            } catch (Exception e) {
                System.err.println("Error executing command: " + e.getMessage());
                e.printStackTrace();
                System.exit(1);
            }
        } else {
            try {
                HackContext context = new HackContext();
                HackIDE ide = new HackIDE(context);
                ide.start();
            } catch (IOException e) {
                System.err.println("Error initializing application: " + e.getMessage());
                e.printStackTrace();
                System.exit(1);
            }
        }
    }
}
