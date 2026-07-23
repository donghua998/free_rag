package com.example.quanxiankongzhi.api.dto;
import lombok.Data;
@Data
public class ApiSearchRequest {
    private String query;   // 查询文本
    private Long kbId;      // 知识库ID
    private Integer topK;   // 返回条数（可选，默认5）
}
