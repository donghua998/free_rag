package com.example.quanxiankongzhi.api.entity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
@Data
@TableName("api_log")
public class ApiLog {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long apiKeyId;       // 关联的 Key

    private String requestPath;  // 请求路径

    private String query;        // 查询内容

    private Long responseTime;   // 响应耗时(ms)

    private String ip;           // 客户端 IP

    private LocalDateTime createTime;
}
