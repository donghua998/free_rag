package com.example.quanxiankongzhi.kb.entity;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
@Data
@Document(indexName = "kb_chunk")
public class ChunkDocument {
    @Id
    private Long id;
    @Field(type = FieldType.Long)
    private Long docId;@Field(type = FieldType.Long)
    private Long kbId;
    @Field(type = FieldType.Integer)
    private Integer chunkIndex;
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String content;
}
