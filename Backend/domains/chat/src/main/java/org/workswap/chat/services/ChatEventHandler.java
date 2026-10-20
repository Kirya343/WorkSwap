package org.workswap.chat.services;

import java.util.List;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.workswap.chat.dto.ChatDetails;
import org.workswap.chat.dto.ChatsLoadedEvent;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ChatEventHandler {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatQueryService chatQueryService;
    
    @EventListener
    @Async
    public void handleChatsLoaded(ChatsLoadedEvent event) {

        List<ChatDetails> details = chatQueryService.getChatDetails(event.authData().sub(), event.chats(), event.locale());

        messagingTemplate.convertAndSendToUser(
            event.authData().sub(), 
            "/queue/chats/details",
            details
        );
    }
}
