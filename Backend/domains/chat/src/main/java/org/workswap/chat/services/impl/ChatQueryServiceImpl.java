package org.workswap.chat.services.impl;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.workswap.chat.dto.ChatDTO;
import org.workswap.chat.dto.ChatDetails;
import org.workswap.chat.dto.ChatMemberDTO;
import org.workswap.chat.dto.ChatsLoadedEvent;
import org.workswap.chat.dto.MessageDTO;
import org.workswap.chat.enums.ChatType;
import org.workswap.chat.services.ChatMappingService;
import org.workswap.chat.services.ChatQueryService;
import org.workswap.listing.datasource.model.Listing;
import org.workswap.listing.datasource.repository.ListingRepository;
import org.workswap.listing.dto.ShortListingDTO;
import org.workswap.listing.services.ListingQueryService;
import org.workswap.sso.security.dto.UserAuthData;
import org.workswap.sso.security.enums.UserStatus;
import org.workswap.user.datasource.model.User;
import org.workswap.user.datasource.repository.UserRepository;
import org.workswap.user.dto.ShortUserDTO;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;

import org.workswap.chat.datasource.model.Chat;
import org.workswap.chat.datasource.model.ChatParticipant;
import org.workswap.chat.datasource.repository.ChatParticipantRepository;
import org.workswap.chat.datasource.repository.ChatRepository;
import org.workswap.chat.datasource.repository.MessageRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Profile({"server"})
public class ChatQueryServiceImpl implements ChatQueryService {

    private static final Logger logger = LoggerFactory.getLogger(ChatQueryService.class);
    
    private final ChatRepository chatRepository;
    private final ChatParticipantRepository chatParticipantRepository;
    private final MessageRepository messageRepository;
    private final ListingRepository listingRepository;

    private final EntityManager entityManager;
    private final ChatMappingService mappingService;
    private final ListingQueryService listingQueryService;
    private final UserRepository userRepository;

    private final ApplicationEventPublisher eventPublisher;
    
    public Chat getOrCreateListingDiscussion(UserAuthData authData, Long listingId) {

        String sellerSub = listingRepository.findAuthorSubByListingId(listingId);

        Optional<Chat> existing = chatRepository.findChatBetweenUsersAndChatTypeAndTargetId(
                sellerSub, authData.sub(), ChatType.LISTING_DISCUSSION, listingId);
        if (existing.isPresent()) {
            return existing.get();
        }

        User sellerProxy = userRepository.findBySub(sellerSub).orElseThrow();
        User clientProxy = userRepository.findBySub(authData.sub()).orElseThrow();

        Set<User> participants = Set.of(clientProxy, sellerProxy);
        Chat listingDiscussion = new Chat(participants, ChatType.LISTING_DISCUSSION, listingId);

        return chatRepository.save(listingDiscussion);
    }

    public Chat getOrCreatePrivateChat(UserAuthData authData, String interlocutorSub) {
        Optional<Chat> existing = chatRepository.findChatBetweenUsersAndChatTypeAndTargetId(
            authData.sub(), interlocutorSub, ChatType.PRIVATE_CHAT, null);
        if (existing.isPresent()) {
            return existing.get();
        }

        User interlocutor = userRepository.findBySub(interlocutorSub).orElseThrow();
        User user = userRepository.findBySub(authData.sub()).orElseThrow();

        Set<User> participants = Set.of(interlocutor, user);
        Chat chat = new Chat(participants, ChatType.PRIVATE_CHAT, null);
        return chatRepository.save(chat);
    }

    public Chat getOrCreateEventChat(UserAuthData authData, Long eventId) {
        if (authData.status().equals(UserStatus.TEMP)) {
            throw new AccessDeniedException("Только для стандартных пользователей");
        }
        Listing event = listingQueryService.getListingById(eventId);
        Optional<Chat> chat = chatRepository.findChatByTargetId(ChatType.EVENT_TOPIC, event.getId());

        User userProxy = entityManager.getReference(User.class, authData.sub());

        if (chat.isPresent()) {
            Chat existing = chat.get();
            
            // TODO Реализовать систему проверки блокировки пользователя в чате перед добавленим его.

            boolean alreadyParticipant = chatParticipantRepository.existsByChatIdAndUserId(existing.getId(), authData.sub());

            if (!alreadyParticipant) {
                existing.getParticipants().add(new ChatParticipant(existing, userProxy));
            }

            return chatRepository.save(existing);
        }

        Chat newChat = new Chat(Set.of(userProxy), ChatType.EVENT_TOPIC, eventId);
        return chatRepository.save(newChat);
    }

    @Transactional
    public List<ChatDTO> getChatsDTOForUser(UserAuthData authData, String locale) {
        List<ChatDTO> chats = chatRepository.findChatsForUser(authData.sub());

        logger.debug("Chats for DTO found: " + chats.size());
        eventPublisher.publishEvent(new ChatsLoadedEvent(authData, chats, locale));
        return chats;
    }

    public List<ChatDetails> getChatDetails(String userSub, List<ChatDTO> chats, String locale) {
        List<Long> chatIds = chats.stream().map(c -> c.id()).toList();
        List<Long> listingIds = chats.stream()
            .filter(c -> Set.of(ChatType.EVENT_TOPIC, ChatType.LISTING_DISCUSSION).contains(c.type()))
            .map(ChatDTO::targetId).toList();

        List<ChatMemberDTO> members = chatParticipantRepository.findMembersByChatIds(chatIds);
        List<ShortListingDTO> listings = listingRepository.findShortListingsByIds(listingIds, userSub, locale);

        List<ChatDetails> chatDetails = chats.stream()
            .map(c -> {
                // участники текущего чата
                List<ShortUserDTO> chatMembers = members.stream()
                    .filter(m -> m.chatId().equals(c.id()))
                    .map(m -> new ShortUserDTO(
                        m.sub(),
                        m.name(),
                        m.avatarUrl()
                    ))
                    .toList();

                // объявление текущего чата (если тип подходящий)
                ShortListingDTO listing = listings.stream()
                    .filter(l -> l.id().equals(c.targetId()) &&
                                Set.of(ChatType.EVENT_TOPIC, ChatType.LISTING_DISCUSSION).contains(c.type()))
                    .findFirst()
                    .orElse(null);

                return new ChatDetails(c.id(), chatMembers, listing);
            }).toList();

        return Objects.requireNonNull(chatDetails);
    }

    public List<MessageDTO> getMessagesByChatId(int page, Long chatId, UserAuthData authData) {
        logger.debug("Получение сообщений для разговора с ID: {}, page: {}", chatId, page);

        if (!chatParticipantRepository.existsByChatIdAndUserId(chatId, authData.sub())) {
            throw new AccessDeniedException("That is not your chat");
        }

        Pageable pageable = PageRequest.of(
            page,
            50,
            Sort.by(Sort.Direction.DESC, "sentAt")
        );

        // Получаем все сообщения для этого разговора
        List<MessageDTO> messages = messageRepository.findByChatId(chatId, pageable).getContent();

        logger.debug("Найдены сообщения {}", messages.size());

        return messages;
    }

    public long getUnreadMessageCount(Long chatId, UserAuthData authData) {
        // Получаем все непрочитанные сообщения для конкретного разговора и пользователя
        return messageRepository.countUnreadsByChatId(chatId, authData.sub());
    }

    public Chat getChatById(Long chatId) {
        if (chatId == null) {
            throw new IllegalStateException("Id чата не найдено");
        }
        return chatRepository.findById(chatId).orElseThrow(
            () -> new EntityNotFoundException("Чата с таким Id не существует"));
    }

    public Boolean isChatTermsAccepted(Long chatId, UserAuthData authData) {
        Boolean accepted = chatParticipantRepository.isChatTermsAccepted(authData.sub(), chatId);
        if (accepted == null) {
            throw new AccessDeniedException("That is not your chat");
        }
        return accepted;
    }

    public List<ShortUserDTO> getChatInterlocutors(Long chatId, UserAuthData authData) {

        ChatType chatType = chatRepository.findTypeById(chatId);

        if (chatType == ChatType.PRIVATE_CHAT || chatType == ChatType.LISTING_DISCUSSION) {
            boolean isParticipant = chatParticipantRepository.existsByChatIdAndUserSubAndChatTypeIn(
                chatId, authData.sub(), List.of(ChatType.PRIVATE_CHAT, ChatType.LISTING_DISCUSSION)
            );
            if (!isParticipant) throw new AccessDeniedException("Нет доступа к приватному чату");
        }

        List<User> interlocutors = chatParticipantRepository.findChatInterlocutorsExcludingUser(chatId, authData.sub());

        return interlocutors.stream().map(user -> ShortUserDTO.ofUser(user)).toList();
    }

    public List<MessageDTO> getChatUnreadMessages(UserAuthData authData) {
        List<MessageDTO> unreads = messageRepository.findUnreadMessagesByUserSub(authData.sub());
        logger.debug("Найдены непрочитанные сообщения для " + authData.sub() + ": " + unreads.size());
        return unreads;
    }

    public ChatDTO getChatDTO(Long chatId, String userSub) {
        Chat chat = getChatById(chatId);
        return mappingService.convertToDTO(chat, userSub);
    }
}
