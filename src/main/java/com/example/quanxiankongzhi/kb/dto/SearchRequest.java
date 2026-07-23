package com.example.quanxiankongzhi.kb.dto;
import lombok.Data;
@Data
public class SearchRequest {
    private String query;
    private Long kbId;
    private Integer topK = 5;
}
