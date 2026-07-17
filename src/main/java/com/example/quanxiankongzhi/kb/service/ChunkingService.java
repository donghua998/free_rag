package com.example.quanxiankongzhi.kb.service;
import com.example.quanxiankongzhi.kb.entity.Chunk;
import com.example.quanxiankongzhi.kb.mapper.ChunkMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Slf4j
@Service
@RequiredArgsConstructor
public class ChunkingService {
    private final ChunkMapper chunkMapper;
    private static final int CHUNK_SIZE = 500;   // 每块500字
    private static final int OVERLAP = 50;// 重叠50字
    private final EmbeddingService embeddingService;
    private final VectorStoreService vectorStoreService;
    /**
     * 将文本分块并存入数据库
     * @param text   原始文本
     * @param docId  文档ID
     * @param kbId   知识库ID
     * @return 分块数量
     */
    public int chunkAndSave(String text, Long docId, Long kbId){
        if (text == null || text.isEmpty()){
            log.warn("文本为空，跳过分块，docId: {}", docId);
            return 0;
        }
        List<String> chunks = splitText(text);
        List<Chunk> chunkEntities = new ArrayList<>();
        for (int i = 0; i < chunks.size(); i++){
            Chunk chunk = new Chunk();
            chunk.setDocId(docId);
            chunk.setKbId(kbId);
            chunk.setChunkIndex(i);
            chunk.setContent(chunks.get(i));
            chunk.setCreateTime(LocalDateTime.now());
            chunkEntities.add(chunk);
        }
        // 批量插入（先删旧数据，再插新数据）
        // 如果文档重新上传，需要清理旧分块
        chunkMapper.delete(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Chunk>()
                .eq(Chunk::getDocId, docId));
        for (Chunk chunk : chunkEntities) {
            chunkMapper.insert(chunk);
            // 向量化并存入 Milvus
            List<Float> vector = embeddingService.embed(chunk.getContent());
            if (!vector.isEmpty()) {
                vectorStoreService.insert(chunk.getId(), docId, kbId, chunk.getContent(), vector);
            }
        }
        log.info("分块完成，docId: {}, 总块数: {}", docId, chunks.size());
        return chunks.size();
    }
    /**
     * 滑动窗口分块
     */
    private List<String> splitText(String text){
        List<String> chunks = new ArrayList<>();
        int start = 0;
        int length = text.length();
        while (start < length){
            int end = Math.min(start + CHUNK_SIZE, length);
            chunks.add(text.substring(start, end));
            start += (CHUNK_SIZE - OVERLAP); // 每次前进 500-50=450 字
        }
        return chunks;
    }
}
