package dumb.code.tui.events;

import java.util.List;

public record PlanGeneratedEvent(List<String> plan) implements UIEvent {}
