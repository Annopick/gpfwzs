package com.gpfwzs.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HuaweiUserInfo {
    private String openId;
    private String unionId;
    private String displayName;
    private String avatar;
}
