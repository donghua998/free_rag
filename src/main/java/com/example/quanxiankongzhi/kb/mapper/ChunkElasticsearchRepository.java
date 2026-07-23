package com.example.quanxiankongzhi.kb.mapper;
import com.example.quanxiankongzhi.kb.entity.ChunkDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import java.util.List;
public interface ChunkElasticsearchRepository extends ElasticsearchRepository<ChunkDocument, Long> {
    // 根据 docId 删除
    void deleteByDocId(Long docId);

    // 根据 kbId 删除
    void deleteByKbId(Long kbId);

    // 全文检索 content 字段
    List<ChunkDocument> findByKbIdAndContent(Long kbId, String content);
}
