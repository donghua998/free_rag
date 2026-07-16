package com.example.quanxiankongzhi.kb.service;
import com.example.quanxiankongzhi.kb.dto.KnowledgeBaseDTO;
import com.example.quanxiankongzhi.kb.dto.KnowledgeBaseVo;

import java.util.List;

public interface KnowledgeBaseService {
    KnowledgeBaseVo create(KnowledgeBaseDTO dto, Long ownerId);
    List<KnowledgeBaseVo> list();
    KnowledgeBaseVo update(Long id, KnowledgeBaseDTO dto);
    void delete(Long id);
}
