package dumb.code;

import dumb.code.tui.Terminal;

public record MessageHandler(Context context) {

    public void addMessage(String sender, String message) {
        Terminal terminal = context.getTerminal();
        if (terminal != null) {
            terminal.addMessage(sender + ": " + message);
        } else {
            System.out.println(sender + ": " + message);
        }
    }

    // Add more methods as needed
}