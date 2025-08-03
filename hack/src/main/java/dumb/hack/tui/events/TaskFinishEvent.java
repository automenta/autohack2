package dumb.hack.tui.events;

public record TaskFinishEvent(boolean success) implements UIEvent {
}
