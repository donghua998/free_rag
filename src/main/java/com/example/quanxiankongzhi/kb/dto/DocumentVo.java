package com.example.quanxiankongzhi.kb.dto;
import lombok.Data;
import java.time.LocalDateTime;
@Data
public class DocumentVo {
    private Long id;
    private Long kbId;
    private String name;
    private String fileType;
    private Long fileSize;
    private String filePath;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
