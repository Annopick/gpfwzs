package com.gpfwzs.dto;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class UserInfoResponse {
    private Long id;
    private String openId;
    private String displayName;
    private String avatar;
}
