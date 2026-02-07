package com.gpfwzs.controller;

import com.gpfwzs.dto.ApiResponse;
import com.gpfwzs.dto.ChatRequest;
import com.gpfwzs.dto.ConversationRequest;
import com.gpfwzs.security.UserPrincipal;
import com.gpfwzs.service.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping(value = "/messages", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter sendMessage(
            @Valid @RequestBody ChatRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        
        return chatService.sendMessage(
                request.getQuery(),
                request.getConversation_id(),
                principal.getId()
        );
    }

    @PostMapping("/conversations")
    public ResponseEntity<ApiResponse<Void>> saveConversation(
            @Valid @RequestBody ConversationRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        
        chatService.saveConversation(principal.getId(), request.getConversation_id());
        return ResponseEntity.ok(ApiResponse.success());
    }
}
