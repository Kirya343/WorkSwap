package org.workswap.chat.datasource.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.workswap.chat.datasource.model.Chat;
import org.workswap.chat.dto.ChatDTO;
import org.workswap.chat.enums.ChatStatus;
import org.workswap.chat.enums.ChatType;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {

    @Query("""
        SELECT cp.chat
        FROM ChatParticipant cp
        WHERE cp.chat.id = :chatId
        AND cp.user.sub = :userSub
    """)
    Optional<Chat> fingByChatIdAndUserId(@Param("chatId") Long chatId, @Param("userSub") String userSub);

    @Query("""
        SELECT c
        FROM Chat c
        WHERE c.chatType = :chatType
        AND (
                (:targetId IS NULL AND c.targetId IS NULL)
                OR c.targetId = :targetId
        )
        AND EXISTS (
                SELECT 1 FROM ChatParticipant p1
                WHERE p1.chat.id = c.id AND p1.user.sub = :userSub1
        )
        AND EXISTS (
                SELECT 1 FROM ChatParticipant p2
                WHERE p2.chat.id = c.id AND p2.user.sub = :userSub2
        )
    """)
    Optional<Chat> findChatBetweenUsersAndChatTypeAndTargetId(
            @Param("userSub1") String userSub1,
            @Param("userSub2") String userSub2,
            @Param("chatType") ChatType chatType,
            @Param("targetId") Long targetId);

    @Query("""
        SELECT c
        FROM Chat c
        WHERE c.chatType = :chatType
        AND c.targetId = :targetId
    """)
    List<Chat> findAllByTarget(
            @Param("chatType") ChatType chatType,
            @Param("targetId") Long targetId);

    @Query("""
        SELECT c
        FROM Chat c
        WHERE c.chatType = :chatType
        AND c.targetId = :targetId
    """)
    Optional<Chat> findChatByTargetId(
            @Param("chatType") ChatType chatType,
            @Param("targetId") Long targetId);

    @Modifying
    @Transactional
    @Query("""
        DELETE FROM Chat c
        WHERE c.status = :status
        AND EXISTS (
            SELECT 1 FROM c.participants p
            WHERE p.id = :userSub
        )
        AND NOT EXISTS (
            SELECT 1 FROM Message m
            WHERE m.chatId = c.id
        )
    """)
    int deleteEmptyChatsByStatus(
        @Param("userSub") String userSub,
        @Param("status") ChatStatus status
    );

    @Modifying
    @Transactional
    @Query("UPDATE Chat c SET c.status = :status WHERE c.id = :chatId")
    void setStatus(@Param("chatId") Long chatId, @Param("status") ChatStatus status);

    @Query("SELECT c.chatType FROM Chat c WHERE c.id = :chatId")
    ChatType findTypeById(@Param("chatId") Long chatId);

    @Query("SELECT cp.chat FROM ChatParticipant cp WHERE cp.user.id = :userId")
    List<Chat> findChatsByUserId(@Param("userId") Long userId);

    @Query("""
        SELECT new org.workswap.chat.dto.ChatDTO(
            c.id,
            SUM(CASE WHEN m.read = false AND m.sender.sub <> :userSub THEN 1 ELSE 0 END),
            lm.text,
            lm.sentAt,
            c.status,
            c.chatType,
            c.targetId
        )
        FROM Chat c
        LEFT JOIN Message m ON m.chatId = c.id
        LEFT JOIN Message lm ON lm.id = (
            SELECT MAX(m2.id)
            FROM Message m2
            WHERE m2.chatId = c.id
        )
        WHERE EXISTS (
            SELECT 1
            FROM ChatParticipant cp
            WHERE cp.chat = c AND cp.user.sub = :userSub
        )
        GROUP BY c.id, lm.text, lm.sentAt, c.status, c.chatType, c.targetId
        ORDER BY lm.sentAt DESC
    """)
    List<ChatDTO> findChatsForUser(@Param("userSub") String userSub);
}
