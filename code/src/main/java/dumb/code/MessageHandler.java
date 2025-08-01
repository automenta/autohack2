package dumb.code;

import dumb.code.tui.Terminal;

public record MessageHandler(Code code) {

    public void onMessage(String message) {
        addMessage("system", message);
    }

    public void addMessage(String sender, String message) {
        Terminal terminal = code.getTerminal();
        if (terminal != null) {
            terminal.addMessage(sender + ": " + message);
        } else {
            System.out.println(sender + ": " + message);
        }
    }

}