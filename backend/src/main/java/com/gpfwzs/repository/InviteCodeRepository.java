package com.gpfwzs.repository;

import com.gpfwzs.entity.InviteCode;
import com.gpfwzs.entity.InviteCodeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface InviteCodeRepository extends JpaRepository<InviteCode, Long> {
    Optional<InviteCode> findByCode(String code);
    Optional<InviteCode> findByCodeAndStatus(String code, InviteCodeStatus status);
    boolean existsByCode(String code);
}
