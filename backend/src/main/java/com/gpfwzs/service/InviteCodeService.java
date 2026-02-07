package com.gpfwzs.service;

import com.gpfwzs.entity.InviteCode;
import com.gpfwzs.entity.InviteCodeStatus;
import com.gpfwzs.exception.InviteCodeException;
import com.gpfwzs.repository.InviteCodeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class InviteCodeService {

    private final InviteCodeRepository inviteCodeRepository;

    @Transactional
    public void validateAndUseInviteCode(String code, String openId) {
        InviteCode inviteCode = inviteCodeRepository.findByCodeAndStatus(code, InviteCodeStatus.UNUSED)
                .orElseThrow(() -> new InviteCodeException("邀请码无效或已使用"));

        inviteCode.setStatus(InviteCodeStatus.USED);
        inviteCode.setUsedByOpenId(openId);
        inviteCode.setUsedAt(LocalDateTime.now());
        inviteCodeRepository.save(inviteCode);

        log.info("Invite code [{}] used by openId: {}", code, openId.substring(0, Math.min(4, openId.length())) + "****");
    }

    public boolean isValidCode(String code) {
        return inviteCodeRepository.findByCodeAndStatus(code, InviteCodeStatus.UNUSED).isPresent();
    }
}
