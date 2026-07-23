package com.example.quanxiankongzhi.api.dto;
import lombok.Data;

import java.time.LocalDateTime;
@Data
public class ApiKeyResponse {
    private Long id;
    private String apiKey;      // 注意：创建成功后只返回一次
    private String name;
    private Integer status;
    private Integer rateLimit;
    private LocalDateTime createTime;
    private LocalDateTime expireTime;
}
