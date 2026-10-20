package org.workswap.user.eventhandlers;

import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.workswap.shared.events.user.UserOnlineState;
import org.workswap.user.services.OnlineCounter;

import lombok.RequiredArgsConstructor;

@Component
@Profile("server")
@RequiredArgsConstructor
public class OnlineStateEventHandler {

    private final OnlineCounter onlineCounter;
    
    @EventListener
    public void handle(UserOnlineState event) {
        if (event.connected()) {
            onlineCounter.userConnected(event.userSub());
        } else {
            onlineCounter.userDisconnected(event.userSub());
        }
    }
}
