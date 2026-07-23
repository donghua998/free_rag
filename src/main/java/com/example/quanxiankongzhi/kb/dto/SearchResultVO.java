package com.example.quanxiankongzhi.kb.dto;
import lombok.Data;
@Data
public class SearchResultVO {
    private Long chunkId;
    private String text;
    private Float score;
    private String source;
}
