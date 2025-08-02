package dumb.code.tui.events;

/**
 * A sealed interface representing all possible events that can be sent from the agent's core logic to the UI.
 * This creates a well-defined, type-safe contract for the event queue.
 */
public sealed interface UIEvent permits
        StatusUpdateEvent,
        PlanGeneratedEvent,
        CommandStartEvent,
        CommandOutputEvent,
        CommandFinishEvent,
        TaskFinishEvent {}
