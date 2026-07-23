package com.example.quanxiankongzhi.api.entity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;
@Data
@TableName("api_key")
public class ApiKey {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String apiKey;       // 32位随机密钥

    private String name;         // 名称

    private Long userId;         // 所属用户

    private Integer status;      // 0=禁用 1=启用

    private Integer rateLimit;   // 每分钟最大请求数

    private LocalDateTime createTime;

    private LocalDateTime expireTime;
}
