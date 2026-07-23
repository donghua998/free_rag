package com.example.quanxiankongzhi.kb.service;
import com.example.quanxiankongzhi.kb.dto.KnowledgeBaseDTO;
import com.example.quanxiankongzhi.kb.dto.KnowledgeBaseVo;
import com.example.quanxiankongzhi.kb.entity.KnowledgeBase;

import java.util.List;

public interface KnowledgeBaseService {
    KnowledgeBaseVo create(KnowledgeBaseDTO dto, Long ownerId);
    List<KnowledgeBaseVo> list(Long userId);
    KnowledgeBaseVo update(Long id, KnowledgeBaseDTO dto, Long userId);
    void delete(Long id, Long userId);
    KnowledgeBase getById(Long id);
}
