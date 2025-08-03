package dumb.code;

import dumb.code.tui.Terminal;

import java.util.ArrayList;
import java.util.List;

public class MessageHandler {

    private Terminal terminal;
    private final List<String> messages = new ArrayList<>();

    public MessageHandler() {
        // constructor is empty
    }

    public void setTerminal(Terminal terminal) {
        this.terminal = terminal;
    }

    public void onMessage(String message) {
        addMessage("system", message);
    }

    public void addMessage(String sender, String message) {
        messages.add(sender + ": " + message);
        if (terminal != null) {
            terminal.addMessage(sender + ": " + message);
        } else {
            System.out.println(sender + ": " + message);
        }
    }

    public List<String> getMessages() {
        return messages;
    }
}