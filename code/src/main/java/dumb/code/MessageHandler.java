package dumb.code;

import java.util.function.Consumer;

public class MessageHandler {
    private Consumer<String> listener;

    public void setListener(Consumer<String> listener) {
        this.listener = listener;
    }

    public void onMessage(String message) {
        if (listener != null) {
            listener.accept(message);
        } else {
            // Fallback for when no UI is attached
            System.out.println("system: " + message);
        }
    }

    public void addMessage(String sender, String message) {
        if (listener != null) {
            listener.accept(sender + ": " + message);
        } else {
            System.out.println(sender + ": " + message);
        }
    }
}