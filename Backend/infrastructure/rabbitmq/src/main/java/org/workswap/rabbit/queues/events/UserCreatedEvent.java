package org.workswap.rabbit.queues.events;

public record UserCreatedEvent(
    String sub,
    String name,
    String email,
    String avatarUrl,
    String status
) {
}