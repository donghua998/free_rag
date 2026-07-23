package com.example.quanxiankongzhi.kb.service.impl;
import com.example.quanxiankongzhi.kb.service.EmbeddingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
@Slf4j
@Service
public class EmbeddingServiceImpl implements EmbeddingService{
    @Value("${embedding.url:http://localhost:8100}")
    private String embeddingUrl;
    private final RestTemplate restTemplate;
    public EmbeddingServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    /**
     * 将文本转为向量
     */
    @Override
    public List<Float> embed(String text){
        try{
            Map<String, Object> request = Map.of("input", text);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.postForObject(
                    embeddingUrl + "/embed", entity, Map.class
            );
            if (response != null && response.containsKey("embedding")){
                @SuppressWarnings("unchecked")
                List<Double> raw = (List<Double>) response.get("embedding");
                return raw.stream().map(Double::floatValue).toList();
            }
            // 兼容 TEI 格式：{"data": [{"embedding": [...]}]}
            if (response != null && response.containsKey("data")){
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> data = (List<Map<String, Object>>) response.get("data");
                if (!data.isEmpty()){
                    @SuppressWarnings("unchecked")
                    List<Double> raw = (List<Double>) data.get(0).get("embedding");
                    return raw.stream().map(Double::floatValue).toList();
                }
            }
        }catch (Exception e){
            log.error("向量化失败: {}", e.getMessage());
        }
        return List.of();
    }
}
