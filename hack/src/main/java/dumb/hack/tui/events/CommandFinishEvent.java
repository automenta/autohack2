package dumb.hack.tui.events;

public record CommandFinishEvent(int commandIndex, boolean success) implements UIEvent {
}
