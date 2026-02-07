package com.gpfwzs.controller;

import com.gpfwzs.dto.ApiResponse;
import com.gpfwzs.dto.UserInfoResponse;
import com.gpfwzs.security.UserPrincipal;
import com.gpfwzs.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserInfoResponse>> getCurrentUser(
            @AuthenticationPrincipal UserPrincipal principal) {
        
        return userService.findById(principal.getId())
                .map(user -> ResponseEntity.ok(ApiResponse.success(userService.toUserInfoResponse(user))))
                .orElse(ResponseEntity.notFound().build());
    }
}
