package com.gpfwzs.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InviteCodeRequest {
    @NotBlank(message = "Invite code is required")
    private String code;
    
    @NotBlank(message = "Pending token is required")
    private String pendingToken;
}
