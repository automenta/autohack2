package dumb.code.tui.events;

public record CommandOutputEvent(int commandIndex, String output) implements UIEvent {}
