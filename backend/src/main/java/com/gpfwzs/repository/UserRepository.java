package com.gpfwzs.repository;

import com.gpfwzs.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByOpenId(String openId);
    boolean existsByOpenId(String openId);
}
