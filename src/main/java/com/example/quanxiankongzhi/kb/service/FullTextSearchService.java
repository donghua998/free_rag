package com.example.quanxiankongzhi.kb.service;
import com.example.quanxiankongzhi.kb.dto.SearchResultVO;
import java.util.List;
public interface FullTextSearchService {
    // 全文检索
    List<SearchResultVO> search(String query, Long kbId, int topK);
    // 索引文档（写入 ES）
    void indexChunk(Long chunkId, Long docId, Long kbId, String content);
    // 删除文档的所有分块
    void deleteByDocId(Long docId);
    // 删除知识库的所有分块
    void deleteByKbId(Long kbId);

}
