package com.pijul.hack;

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
                Container container = new Container();
                container.setMessageHandler(System.out::println);
                container.init();
                container.getCommandManager().processInput(command);
            } catch (Exception e) {
                System.err.println("Error executing command: " + e.getMessage());
                e.printStackTrace();
                System.exit(1);
            }
        } else {
            try {
                Container container = new Container();
                HackIDE ide = new HackIDE(container);
                ide.start();
            } catch (IOException e) {
                System.err.println("Error initializing application: " + e.getMessage());
                e.printStackTrace();
                System.exit(1);
            }
        }
    }
}
