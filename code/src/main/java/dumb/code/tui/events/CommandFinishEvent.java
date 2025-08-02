package dumb.code.tui.events;

public record CommandFinishEvent(int commandIndex, boolean success) implements UIEvent {}
