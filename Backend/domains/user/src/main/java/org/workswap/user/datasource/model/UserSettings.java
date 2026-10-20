package org.workswap.user.datasource.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor
public class UserSettings {

    public UserSettings(User user) {
        this.user = user;
    }

    @Id
    private Long id;

    @OneToOne
    @MapsId
    private User user;

    @Setter
    private String avatarType = "google";

    @Setter
    private String googleAvatar;

    @Setter
    private String uploadedAvatar;
    
    @Setter
    private boolean phoneVisible = true;  // Скрывать или отображать телефон

    @Setter
    private boolean emailVisible = true;  // Скрывать или отображать email

    @Setter
    private boolean telegramConnected = false; // Подключен ли Telegram
}