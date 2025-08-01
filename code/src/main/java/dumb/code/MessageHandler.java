package dumb.code;

import dumb.code.tui.Terminal;

public record MessageHandler(Code code) {

    public void addMessage(String sender, String message) {
        Terminal terminal = code.getTerminal();
        if (terminal != null) {
            terminal.addMessage(sender + ": " + message);
        } else {
            System.out.println(sender + ": " + message);
        }
    }

    public String promptUser(String message) {
        Terminal terminal = code.getTerminal();
        if (terminal != null) {
            // This is a blocking call, which might be an issue for the TUI event loop.
            // For now, let's assume it works.
            return terminal.readLine(message);
        } else {
            System.out.println(message);
            java.util.Scanner scanner = new java.util.Scanner(System.in);
            return scanner.nextLine();
        }
    }
}