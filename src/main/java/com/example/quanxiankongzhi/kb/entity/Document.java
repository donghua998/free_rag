package com.example.quanxiankongzhi.kb.entity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
@Data
@TableName("kb_document")
public class Document {
    @TableId(type = IdType.AUTO)
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
