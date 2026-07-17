package com.example.quanxiankongzhi.kb.service.impl;
import com.example.quanxiankongzhi.kb.dto.KnowledgeBaseDTO;
import com.example.quanxiankongzhi.kb.dto.KnowledgeBaseVo;
import com.example.quanxiankongzhi.kb.entity.KnowledgeBase;
import com.example.quanxiankongzhi.kb.mapper.KnowledgeBaseMapper;
import lombok.RequiredArgsConstructor;
import com.example.quanxiankongzhi.kb.service.KnowledgeBaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
@Slf4j
@Service
@RequiredArgsConstructor
public class KnowledgeBaseServiceImpl implements KnowledgeBaseService {
    private final KnowledgeBaseMapper knowledgeBaseMapper;
    @Override
    public KnowledgeBaseVo create(KnowledgeBaseDTO dto, Long ownerId) {
        // ① DTO → Entity
        KnowledgeBase kb = new KnowledgeBase();
        kb.setName(dto.getName());
        kb.setDescription(dto.getDescription());
        kb.setOwnerId(ownerId);
        kb.setStatus(1);
        kb.setCreateTime(LocalDateTime.now());
        kb.setUpdateTime(LocalDateTime.now());

        // ② 插入数据库
        knowledgeBaseMapper.insert(kb);

        // ③ Entity → VO 返回
        return toVo(kb);
    }
    @Override
    public List<KnowledgeBaseVo> list() {
        List<KnowledgeBase> list = knowledgeBaseMapper.selectList(null);
        return list.stream()
                .map(this::toVo)
                .collect(Collectors.toList());
    }
    @Override
    public KnowledgeBaseVo update(Long id, KnowledgeBaseDTO dto) {
        // ① 先查出来
        KnowledgeBase kb = knowledgeBaseMapper.selectById(id);
        if (kb == null) {
            throw new RuntimeException("知识库不存在");
        }

        // ② 只更新有变化的字段
        kb.setName(dto.getName());
        kb.setDescription(dto.getDescription());
        kb.setUpdateTime(LocalDateTime.now());

        // ③ 更新数据库
        knowledgeBaseMapper.updateById(kb);

        return toVo(kb);
    }
    @Override
    public void delete(Long id) {
        KnowledgeBase kb = knowledgeBaseMapper.selectById(id);
        if (kb == null) {
            throw new RuntimeException("知识库不存在");
        }
        knowledgeBaseMapper.deleteById(id);
    }
    private KnowledgeBaseVo toVo(KnowledgeBase kb) {
        KnowledgeBaseVo vo = new KnowledgeBaseVo();
        vo.setId(kb.getId());
        vo.setName(kb.getName());
        vo.setDescription(kb.getDescription());
        vo.setOwnerId(kb.getOwnerId());
        vo.setStatus(kb.getStatus());
        vo.setCreateTime(kb.getCreateTime());
        vo.setUpdateTime(kb.getUpdateTime());
        return vo;
    }
}
