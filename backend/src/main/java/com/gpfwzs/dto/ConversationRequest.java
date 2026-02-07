package com.gpfwzs.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ConversationRequest {
    @NotBlank(message = "Conversation ID cannot be empty")
    private String conversation_id;
}
