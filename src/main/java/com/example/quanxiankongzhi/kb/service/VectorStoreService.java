package com.example.quanxiankongzhi.kb.service;
import java.util.List;
import com.example.quanxiankongzhi.kb.dto.SearchResultVO;
public interface VectorStoreService{
    void init();
    void insert(Long chunkId, Long docId, Long kbId, String text, List<Float> vector);
    List<SearchResultVO> search(List<Float> queryVector, Long kbId, int topK);
}