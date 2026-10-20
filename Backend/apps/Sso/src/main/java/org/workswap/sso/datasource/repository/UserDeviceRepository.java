package org.workswap.sso.datasource.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.workswap.sso.datasource.model.User;
import org.workswap.sso.datasource.model.UserDevice;

@Repository
public interface UserDeviceRepository extends JpaRepository<UserDevice, Long> {
    Optional<UserDevice> findFirstByFingerprintAndIpAndUserAgentAndTempTrue(
        String fingerprint,
        String ip,
        String userAgent
    );
    Optional<UserDevice> findByFingerprintAndIpAndUserAgentAndUser(
        String fingerprint,
        String ip,
        String userAgent,
        User user
    );

    Optional<UserDevice> findFirstByFingerprintAndUserAgentAndTempTrue(
        String fingerprint,
        String userAgent
    );

    Optional<UserDevice> findByFingerprintAndUserAgentAndUser(
        String fingerprint,
        String userAgent,
        User user
    );

    @Query("""
        select d
        from UserDevice d
        where d.user = :user
        and d.fingerprint = :fingerprint
        and d.userAgent = :userAgent
        and (:ip is null or d.ip = :ip)
    """)
    Optional<UserDevice> findDevice(
        @Param("user") User user,
        @Param("fingerprint") String fingerprint,
        @Param("userAgent") String userAgent,
        @Param("ip") String ip
    );

    @Query("""
        select d
        from UserDevice d
        join fetch d.user u
        where u.status = 'TEMP'
        and d.fingerprint = :fingerprint
        and d.userAgent = :userAgent
        and (:ip is null or d.ip = :ip)
        order by d.lastSeen desc
    """)
    Optional<UserDevice> findTempDevice(
        @Param("fingerprint") String fingerprint,
        @Param("userAgent") String userAgent,
        @Param("ip") String ip
    );
}
