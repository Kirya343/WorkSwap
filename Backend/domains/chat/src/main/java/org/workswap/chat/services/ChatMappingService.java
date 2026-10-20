package org.workswap.chat.services;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.workswap.chat.dto.ChatDTO;
import org.workswap.chat.enums.ChatStatus;
import org.workswap.chat.enums.ChatType;
import org.workswap.chat.datasource.model.Chat;
import org.workswap.chat.datasource.model.Message;
import org.workswap.chat.datasource.repository.MessageRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Profile({"server", "statistic"})
public class ChatMappingService {

    private final MessageRepository messageRepository;

    public ChatDTO convertToDTO(Chat chat, String userSub) {
        log.debug("Конвертация в дто начата разговора: " + chat.getId());

        ChatStatus status = chat.getStatus();
        ChatType type = chat.getChatType();

        log.debug("Определяем, есть ли новые сообщения");

        long unreadcount = messageRepository.countByChatIdAndSenderSubNotAndReadFalse(chat.getId(), userSub);

        log.debug("Обработка последнего сообщения");
        // Обработка последнего сообщения
        Optional<Message> lastMessage = messageRepository.findTopByChatIdOrderByIdDesc(chat.getId());
        String lastMessagePreview = null;
        LocalDateTime lastMessageTime = null;

        if (lastMessage.isPresent()) {
            Message existing = lastMessage.get();
            lastMessagePreview = existing.getText();
            lastMessageTime = existing.getSentAt();
        } else {
            lastMessageTime = chat.getCreatedAt();
        }

        ChatDTO dto = new ChatDTO(
            chat.getId(),
            unreadcount,
            lastMessagePreview,
            lastMessageTime,
            status,
            type,
            chat.getTargetId()
        );

        log.debug("Конвертация закончена");

        return dto;
    }

}
