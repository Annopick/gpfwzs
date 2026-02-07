package com.gpfwzs.service;

import com.gpfwzs.dto.HuaweiUserInfo;
import com.gpfwzs.dto.UserInfoResponse;
import com.gpfwzs.entity.User;
import com.gpfwzs.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByOpenId(String openId) {
        return userRepository.findByOpenId(openId);
    }

    public User createOrUpdateUser(HuaweiUserInfo userInfo) {
        return userRepository.findByOpenId(userInfo.getOpenId())
                .map(existingUser -> {
                    // Update existing user
                    existingUser.setDisplayName(userInfo.getDisplayName());
                    existingUser.setUnionId(userInfo.getUnionId());
                    existingUser.setAvatar(userInfo.getAvatar());
                    return userRepository.save(existingUser);
                })
                .orElseGet(() -> {
                    // Create new user
                    User user = new User();
                    user.setOpenId(userInfo.getOpenId());
                    user.setUnionId(userInfo.getUnionId());
                    user.setDisplayName(userInfo.getDisplayName());
                    user.setAvatar(userInfo.getAvatar());
                    return userRepository.save(user);
                });
    }

    public UserInfoResponse toUserInfoResponse(User user) {
        String maskedOpenId = maskOpenId(user.getOpenId());
        return UserInfoResponse.builder()
                .id(user.getId())
                .openId(maskedOpenId)
                .displayName(user.getDisplayName())
                .avatar(user.getAvatar())
                .build();
    }

    private String maskOpenId(String openId) {
        if (openId == null || openId.length() <= 8) {
            return openId;
        }
        return openId.substring(0, 4) + "****" + openId.substring(openId.length() - 4);
    }
}
