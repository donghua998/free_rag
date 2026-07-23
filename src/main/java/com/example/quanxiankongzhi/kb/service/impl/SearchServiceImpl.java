package com.example.quanxiankongzhi.kb.service.impl;
import com.example.quanxiankongzhi.kb.dto.SearchRequest;
import com.example.quanxiankongzhi.kb.dto.SearchResultVO;
import com.example.quanxiankongzhi.kb.service.EmbeddingService;
import com.example.quanxiankongzhi.kb.service.FullTextSearchService;
import com.example.quanxiankongzhi.kb.service.SearchService;
import com.example.quanxiankongzhi.kb.service.VectorStoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
@Slf4j
@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {
    private final EmbeddingService embeddingService;
    private final VectorStoreService vectorStoreService;
    private final FullTextSearchService fullTextSearchService;
    @Override
    public List<SearchResultVO> search(SearchRequest request){
        // 1. 参数校验
        if (request.getQuery() == null || request.getQuery().isEmpty()){
            log.warn("搜索查询为空");
            return List.of();
        }
        if (request.getKbId() == null){
            log.warn("知识库ID为空");
            return List.of();
        }
        int topK = request.getTopK() != null ? request.getTopK() : 5;
        // 2. 向量检索
        List<Float> vector = embeddingService.embed(request.getQuery());
        List<SearchResultVO> vectorResults = List.of();
        if (!vector.isEmpty()){
            vectorResults = vectorStoreService.search(vector, request.getKbId(), topK);
            vectorResults.forEach(r -> r.setSource("vector"));
        }
        // 3. 全文检索
        List<SearchResultVO> fulltextResults = fullTextSearchService.search(
                request.getQuery(), request.getKbId(), topK);
        // 4. RRF 融合排序
        List<SearchResultVO> merged = reciprocalRankFusion(vectorResults, fulltextResults, topK);
        log.info("混合检索完成，kbId: {}, 查询: {}, 向量结果: {}, 全文结果: {}, 最终结果: {}",
                request.getKbId(), request.getQuery(),
                vectorResults.size(), fulltextResults.size(), merged.size());
        return merged;
    }
    /**
     * Reciprocal Rank Fusion 融合排序
     */
    private List<SearchResultVO> reciprocalRankFusion(
            List<SearchResultVO> vectorResults,
            List<SearchResultVO> fulltextResults,
            int topK){
        final int K = 60; // RRF 常量
        Map<Long, Double> scoreMap = new HashMap<>();
        Map<Long, SearchResultVO> resultMap = new HashMap<>();
        // 处理向量检索结果
        for (int i = 0; i < vectorResults.size(); i++){
            SearchResultVO dto = vectorResults.get(i);
            double rrfScore = 1.0 / (K + i + 1);
            scoreMap.merge(dto.getChunkId(), rrfScore, Double::sum);
            resultMap.putIfAbsent(dto.getChunkId(), dto);
        }
        // 处理全文检索结果
        for (int i = 0; i < fulltextResults.size(); i++) {
            SearchResultVO dto = fulltextResults.get(i);
            double rrfScore = 1.0 / (K + i + 1);
            scoreMap.merge(dto.getChunkId(), rrfScore, Double::sum);
            resultMap.putIfAbsent(dto.getChunkId(), dto);
        }
        // 按 RRF 分数排序，取 topK
        return scoreMap.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .limit(topK)
                .map(entry -> {
                    SearchResultVO dto = resultMap.get(entry.getKey());
                    dto.setScore(entry.getValue().floatValue()); // 更新为 RRF 分数
                    dto.setSource("hybrid");
                    return dto;
                })
                .collect(Collectors.toList());
    }
}
