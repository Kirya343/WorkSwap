package org.workswap.chat.controllers;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.workswap.chat.services.ChatCommandService;
import org.workswap.chat.services.ChatQueryService;
import org.workswap.sso.security.annotations.controllers.Authenticated;
import org.workswap.sso.security.annotations.controllers.RequiredPermission;
import org.workswap.sso.security.annotations.parameters.AuthUser;
import org.workswap.sso.security.dto.UserAuthData;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatQueryService chatQueryService;
    private final ChatCommandService chatCommandService;

    @GetMapping("/listing-discussion")
    @Authenticated
    public Long getOrCreateListingDiscussion(
        @RequestParam Long listingId,
        @AuthUser UserAuthData authData
    ) {
        return chatQueryService.getOrCreateListingDiscussion(authData, listingId).getId();
    }

    @GetMapping("/private-chat")
    @Authenticated
    public Long getOrCreatePrivateChat(
        @RequestParam String interlocutorSub,
        @AuthUser UserAuthData authData
    ) {
        return chatQueryService.getOrCreatePrivateChat(authData, interlocutorSub).getId();
    }

    @GetMapping("/event-chat")
    @Authenticated
    public Long getOrCreateEventChat(
        @RequestParam Long eventId,
        @AuthUser UserAuthData authData
    ) {
        return chatQueryService.getOrCreateEventChat(authData, eventId).getId();
    }

    @PatchMapping("/{chatid}/chat-terms")
    @RequiredPermission("CHAT_ACCEPT_TERMS")
    public boolean getTermsState(
        @PathVariable Long chatId, 
        @AuthUser UserAuthData authData
    ) {
        return chatQueryService.isChatTermsAccepted(chatId, authData);
    }

    @PatchMapping("/{chatId}/accept-terms")
    @RequiredPermission("CHAT_ACCEPT_TERMS")
    public void acceptTerms(@PathVariable Long chatId, @AuthUser UserAuthData authData) {
        chatCommandService.acceptChatTerms(chatId, authData);
    }

    @DeleteMapping("/temporary")
    @RequiredPermission("CLEAR_TEMPORARY_CHATS")
    public void deleteTemporaryChat(@AuthUser UserAuthData authData) {
        chatCommandService.deleteTemporaryChats(authData);
    }
}

