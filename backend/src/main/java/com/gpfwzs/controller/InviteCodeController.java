package com.gpfwzs.controller;

import com.gpfwzs.dto.ApiResponse;
import com.gpfwzs.dto.InviteCodeRequest;
import com.gpfwzs.exception.InviteCodeException;
import com.gpfwzs.exception.OAuthException;
import com.gpfwzs.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/invite-codes")
@RequiredArgsConstructor
public class InviteCodeController {

    private final AuthService authService;

    @PostMapping("/validate")
    public ResponseEntity<ApiResponse<Map<String, String>>> validateInviteCode(
            @Valid @RequestBody InviteCodeRequest request) {
        try {
            String token = authService.processInviteCodeLogin(request.getCode(), request.getPendingToken());
            return ResponseEntity.ok(ApiResponse.success(Map.of("token", token)));
        } catch (InviteCodeException e) {
            log.warn("Invite code validation failed: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, e.getMessage()));
        } catch (OAuthException e) {
            log.error("Auth processing failed: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, e.getMessage()));
        }
    }
}
