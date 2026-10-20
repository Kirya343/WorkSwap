package org.workswap.chat.datasource.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.workswap.chat.datasource.model.Message;
import org.workswap.chat.dto.MessageDTO;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    // Получить все непрочитанные сообщения для пользователя
    List<Message> findBySenderSubNotAndReadFalse(String senderSub);

    // Получить все непрочитанные сообщения для пользователя в конкретном разговоре
    @Query("""
        SELECT new org.workswap.chat.dto.MessageDTO(
            m.id,
            m.text,
            m.sentAt,
            m.sender.sub,
            m.chat.id,
            m.read
        )
        FROM Message m
        WHERE m.chat.id = :chatId AND m.sender.sub <> :senderSub AND m.read = FALSE
        """)
    List<MessageDTO> findUnreadsByChatId(
        @Param("chatId") Long chatId, 
        @Param("senderSub") String senderSub);

    @Query("""
        SELECT COUNT(m)
        FROM Message m
        WHERE m.chat.id = :chatId AND m.sender.sub <> :senderSub AND m.read = FALSE
        """)
    long countUnreadsByChatId(
        @Param("chatId") Long chatId, 
        @Param("senderSub") String senderSub);

    List<Message> findByChatIdOrderBySentAtAsc(Long chatId);

    @Query("""
        SELECT new org.workswap.chat.dto.MessageDTO(
            m.id,
            m.text,
            m.sentAt,
            m.sender.sub,
            m.chat.id,
            m.read
        )
        FROM Message m
        WHERE m.chat.id = :chatId
        """)
    Page<MessageDTO> findByChatId(@Param("chatId") Long chatId, Pageable pageable);

    long countByChatIdAndSenderSubNotAndReadFalse(Long chatId, String senderSub);

    // Новый метод: получить сообщения по ID разговора (с сортировкой по времени)
    Page<Message> findByChatIdOrderBySentAtDesc(Long chatId, Pageable pageable);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        UPDATE Message m
        SET m.read = true
        WHERE m.chatId = :chatId
        AND m.sender.sub <> :userSub
        AND m.read = false
    """)
    void markMessagesAsRead(
            @Param("chatId") Long chatId,
            @Param("userSub") String userSub
    );

    @Query("""
        SELECT new org.workswap.chat.dto.MessageDTO(
            m.id,
            m.text,
            m.sentAt,
            m.sender.sub,
            m.chat.id,
            m.read
        )
        FROM Message m
        WHERE m.chatId IN (
            SELECT cp.chat.id
            FROM ChatParticipant cp
            WHERE cp.user.sub = :userSub
        )
        AND m.read = false
        AND m.sender.sub <> :userSub
    """)
    List<MessageDTO> findUnreadMessagesByUserSub(@Param("userSub") String userSub);

    Optional<Message> findTopByChatIdOrderByIdDesc(Long chatId);
}


