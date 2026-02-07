package com.gpfwzs.repository;

import com.gpfwzs.entity.Whitelist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface WhitelistRepository extends JpaRepository<Whitelist, Long> {
    boolean existsByOpenId(String openId);
    Optional<Whitelist> findByOpenId(String openId);
}
