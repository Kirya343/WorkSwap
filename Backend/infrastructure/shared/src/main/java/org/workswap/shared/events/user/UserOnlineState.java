package org.workswap.shared.events.user;

public record UserOnlineState(
    String userSub,
    boolean connected
) {
}
