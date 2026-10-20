package org.workswap.user.datasource.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.workswap.user.datasource.model.UserSettings;

@Repository
public interface UserSettingsRepository extends JpaRepository<UserSettings, Long> {
    boolean existsByUserSubAndTelegramConnectedTrue(String userSub);
}
