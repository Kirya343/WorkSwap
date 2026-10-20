package org.workswap.chat.services;

import java.util.List;

import org.workswap.chat.datasource.model.Chat;
import org.workswap.chat.dto.ChatDTO;
import org.workswap.chat.dto.ChatDetails;
import org.workswap.chat.dto.MessageDTO;
import org.workswap.sso.security.dto.UserAuthData;
import org.workswap.user.dto.ShortUserDTO;

public interface ChatQueryService {
    
    Chat getOrCreateListingDiscussion(UserAuthData authData, Long listingId);
    Chat getOrCreatePrivateChat(UserAuthData authData, String interlocutorSub);
    Chat getOrCreateEventChat(UserAuthData authData, Long eventId);

    ChatDTO getChatDTO(Long chatId, String userSub);
    List<ChatDTO> getChatsDTOForUser(UserAuthData authData, String locale);
    List<MessageDTO> getMessagesByChatId(int page, Long chatId, UserAuthData authData);
    List<MessageDTO> getChatUnreadMessages(UserAuthData authData);

    long getUnreadMessageCount(Long chatId, UserAuthData authData);
    Chat getChatById(Long chatId);

    Boolean isChatTermsAccepted(Long chatId, UserAuthData authData);

    List<ShortUserDTO> getChatInterlocutors(Long chatId, UserAuthData authData);

    List<ChatDetails> getChatDetails(String userSub, List<ChatDTO> chats, String locale);
}
