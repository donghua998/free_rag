package com.example.quanxiankongzhi.api.dto;
import lombok.Data;
@Data
public class ApiKeyRequest {
    private String name;        // Key 名称
    private Integer rateLimit;  // 限流（可选，默认30）
}
