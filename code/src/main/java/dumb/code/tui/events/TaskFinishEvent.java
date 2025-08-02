package dumb.code.tui.events;

public record TaskFinishEvent(boolean success) implements UIEvent {}
