package com.example.quanxiankongzhi.kb.dto;
import lombok.Data;
import java.time.LocalDateTime;
@Data
public class KnowledgeBaseVo {
    private Long id;
    private String name;
    private String description;
    private Long ownerId;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
