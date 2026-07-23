package com.example.quanxiankongzhi.kb.service;
import com.example.quanxiankongzhi.kb.dto.SearchRequest;
import com.example.quanxiankongzhi.kb.dto.SearchResultVO;
import java.util.List;
public interface SearchService {
    List<SearchResultVO> search(SearchRequest request);
}
