package com.gpfwzs.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChatRequest {
    @NotBlank(message = "Query cannot be empty")
    private String query;
    
    private String conversation_id;
}
