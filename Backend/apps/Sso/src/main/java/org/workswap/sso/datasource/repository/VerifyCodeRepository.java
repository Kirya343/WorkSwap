package org.workswap.sso.datasource.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.workswap.sso.datasource.model.VerifyCode;

@Repository
public interface VerifyCodeRepository extends JpaRepository<VerifyCode, Long> {
    
    VerifyCode findByEmail(String email);
    boolean existsByEmailAndCode(String email, String code);
    void deleteByEmail(String email);
}
