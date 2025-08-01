package dumb.hack.tui;

import com.googlecode.lanterna.input.KeyStroke;
import java.util.HashMap;
import java.util.Map;

public class KeybindingManager {
    private final Map<KeyStroke, Runnable> keybindings = new HashMap<>();

    public void register(KeyStroke keyStroke, Runnable action) {
        keybindings.put(keyStroke, action);
    }

    public boolean handle(KeyStroke keyStroke) {
        Runnable action = keybindings.get(keyStroke);
        if (action != null) {
            action.run();
            return true;
        }
        return false;
    }
}
