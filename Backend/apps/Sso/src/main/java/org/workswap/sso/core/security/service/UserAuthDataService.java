package org.workswap.sso.core.security.service;

import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.workswap.sso.datasource.model.User;
import org.workswap.sso.datasource.repository.UserRepository;
import org.workswap.sso.security.dto.UserAuthData;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserAuthDataService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public UserAuthData load(Long userId) {

        User user = userRepository.findById(userId).orElseThrow();

        return new UserAuthData(
            Objects.requireNonNull(user.getSub()),
            Objects.requireNonNull(user.getStatus())
        );
    }
}