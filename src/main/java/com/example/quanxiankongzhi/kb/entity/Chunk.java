package com.example.quanxiankongzhi.kb.entity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
@Data
@TableName("kb_chunk")
public class Chunk {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long docId;

    private Long kbId;

    private Integer chunkIndex;

    private String content;

    private LocalDateTime createTime;
}
