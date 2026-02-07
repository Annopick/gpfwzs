package com.gpfwzs.service;

import com.gpfwzs.entity.Conversation;
import com.gpfwzs.repository.ConversationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final WebClient webClient;
    private final ConversationRepository conversationRepository;

    @Value("${dify.api-url}")
    private String difyApiUrl;

    @Value("${dify.api-key}")
    private String difyApiKey;

    public SseEmitter sendMessage(String query, String conversationId, Long userId) {
        SseEmitter emitter = new SseEmitter(300000L); // 5 minutes timeout

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("query", query);
        requestBody.put("user", String.valueOf(userId));
        requestBody.put("response_mode", "streaming");
        requestBody.put("inputs", Map.of());
        
        if (conversationId != null && !conversationId.isEmpty()) {
            requestBody.put("conversation_id", conversationId);
        }

        Flux<String> responseFlux = webClient.post()
                .uri(difyApiUrl + "/chat-messages")
                .header("Authorization", "Bearer " + difyApiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToFlux(String.class);

        responseFlux.subscribe(
                data -> {
                    try {
                        emitter.send(SseEmitter.event().data(data));
                    } catch (IOException e) {
                        log.error("Error sending SSE data", e);
                        emitter.completeWithError(e);
                    }
                },
                error -> {
                    log.error("Error in SSE stream", error);
                    try {
                        emitter.send(SseEmitter.event()
                                .data("{\"event\":\"error\",\"message\":\"" + error.getMessage() + "\"}"));
                    } catch (IOException e) {
                        log.error("Error sending error event", e);
                    }
                    emitter.completeWithError(error);
                },
                () -> {
                    log.debug("SSE stream completed");
                    emitter.complete();
                }
        );

        emitter.onCompletion(() -> log.debug("SSE emitter completed"));
        emitter.onTimeout(() -> {
            log.warn("SSE emitter timeout");
            emitter.complete();
        });
        emitter.onError(e -> log.error("SSE emitter error", e));

        return emitter;
    }

    public void saveConversation(Long userId, String conversationId) {
        Conversation conversation = conversationRepository
                .findByUserIdAndConversationId(userId, conversationId)
                .orElseGet(() -> {
                    Conversation newConv = new Conversation();
                    newConv.setUserId(userId);
                    newConv.setConversationId(conversationId);
                    return newConv;
                });
        
        conversationRepository.save(conversation);
    }
}
