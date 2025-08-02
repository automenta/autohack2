package dumb.code;

import dumb.code.tui.Terminal;

import java.util.ArrayList;
import java.util.List;

public class MessageHandler {

    private final Code code;
    private final List<String> messages = new ArrayList<>();

    public MessageHandler(Code code) {
        this.code = code;
    }

    public void onMessage(String message) {
        addMessage("system", message);
    }

    public void addMessage(String sender, String message) {
        messages.add(sender + ": " + message);
        Terminal terminal = code.getTerminal();
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