package com.example.quanxiankongzhi.kb.service.impl;
import com.example.quanxiankongzhi.kb.dto.SearchResultVO;
import com.example.quanxiankongzhi.kb.entity.ChunkDocument;
import com.example.quanxiankongzhi.kb.mapper.ChunkElasticsearchRepository;
import com.example.quanxiankongzhi.kb.service.FullTextSearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Slf4j
@Service
@RequiredArgsConstructor
public class FullTextSearchServiceImpl implements FullTextSearchService {
    private final ChunkElasticsearchRepository repository;
    private final ElasticsearchOperations elasticsearchOperations;
    @Override
    public List<SearchResultVO> search(String query, Long kbId, int topK){
        try{
            // 构建查询：匹配 kbId + content 全文检索
            Query searchQuery = NativeQuery.builder()
                    .withQuery(q -> q.bool(b -> b
                            .must(m -> m.term(t -> t.field("kbId").value(kbId)))
                            .must(m -> m.match(mt -> mt.field("content").query(query)))
                    ))
                    .withMaxResults(topK)
                    .build();
            SearchHits<ChunkDocument> hits = elasticsearchOperations.search(searchQuery, ChunkDocument.class);
            List<SearchResultVO> results = new ArrayList<>();
            for (SearchHit<ChunkDocument> hit : hits){
                ChunkDocument doc = hit.getContent();
                SearchResultVO dto = new SearchResultVO();
                dto.setChunkId(doc.getId());
                dto.setText(doc.getContent());
                dto.setScore(hit.getScore().floatValue());
                dto.setSource("fulltext");
                results.add(dto);
            }
            log.info("ES全文检索完成，query: {}, kbId: {}, 结果数: {}", query, kbId, results.size());
            return results;
        }catch (Exception e){
            log.error("ES全文检索失败: {}", e.getMessage());
            return List.of();
        }
    }
    @Override
    public void indexChunk(Long chunkId, Long docId, Long kbId, String content){
        try{
            ChunkDocument doc = new ChunkDocument();
            doc.setId(chunkId);
            doc.setDocId(docId);
            doc.setKbId(kbId);
            doc.setContent(content);
            repository.save(doc);
        } catch (Exception e){
            log.error("ES索引失败, chunkId: {}, error: {}", chunkId, e.getMessage());
        }
    }
    @Override
    public void deleteByDocId(Long docId){
        try{
            repository.deleteByDocId(docId);
            log.info("ES删除文档分块完成, docId: {}", docId);
        }catch (Exception e){
            log.error("ES删除文档分块失败, docId: {}, error: {}", docId, e.getMessage());
        }
    }
    @Override
    public void deleteByKbId(Long kbId){
        try{
            repository.deleteByKbId(kbId);
            log.info("ES删除知识库分块完成, kbId: {}", kbId);
        } catch (Exception e){
            log.error("ES删除知识库分块失败, kbId: {}, error: {}", kbId, e.getMessage());
        }
    }
}

