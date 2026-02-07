package com.gpfwzs.service;

import com.gpfwzs.entity.Whitelist;
import com.gpfwzs.repository.WhitelistRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class WhitelistService {

    private final WhitelistRepository whitelistRepository;

    public boolean isInWhitelist(String openId) {
        return whitelistRepository.existsByOpenId(openId);
    }

    @Transactional
    public void addToWhitelist(String openId, String description) {
        if (whitelistRepository.existsByOpenId(openId)) {
            log.info("OpenId already in whitelist: {}", openId.substring(0, Math.min(4, openId.length())) + "****");
            return;
        }

        Whitelist whitelist = new Whitelist();
        whitelist.setOpenId(openId);
        whitelist.setDescription(description);
        whitelistRepository.save(whitelist);

        log.info("Added openId to whitelist: {}", openId.substring(0, Math.min(4, openId.length())) + "****");
    }
}
