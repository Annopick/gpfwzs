package com.gpfwzs.controller;

import com.gpfwzs.dto.ApiResponse;
import com.gpfwzs.exception.OAuthException;
import com.gpfwzs.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Value("${frontend.url}")
    private String frontendUrl;

    /**
     * 获取华为OAuth授权URL
     */
    @GetMapping("/oauth-url")
    public ResponseEntity<ApiResponse<Map<String, String>>> getOAuthUrl() {
        String authUrl = authService.buildOAuthAuthorizeUrl();
        return ResponseEntity.ok(ApiResponse.success(Map.of("url", authUrl)));
    }

    /**
     * OAuth 回调处理
     * 成功时返回 token 或 pending_token（需要邀请码）
     */
    @GetMapping("/callback")
    public ResponseEntity<Void> handleOAuthCallback(
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String error) {
        
        if (error != null) {
            log.warn("OAuth error: {}", error);
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create(frontendUrl + "/callback?error=" + error))
                    .build();
        }

        if (code == null || code.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create(frontendUrl + "/callback?error=missing_code"))
                    .build();
        }

        try {
            AuthService.OAuthResult result = authService.processOAuthCallback(code);
            
            if (result.isSuccess()) {
                // User in whitelist, return JWT token
                return ResponseEntity.status(HttpStatus.FOUND)
                        .location(URI.create(frontendUrl + "/callback?token=" + result.getToken()))
                        .build();
            } else {
                // Not in whitelist, return pending token for invite code flow
                String encodedToken = URLEncoder.encode(result.getPendingToken(), StandardCharsets.UTF_8);
                return ResponseEntity.status(HttpStatus.FOUND)
                        .location(URI.create(frontendUrl + "/callback?need_invite=true&pending_token=" + encodedToken))
                        .build();
            }
        } catch (OAuthException e) {
            log.error("OAuth processing failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create(frontendUrl + "/callback?error=oauth_failed"))
                    .build();
        }
    }
}
