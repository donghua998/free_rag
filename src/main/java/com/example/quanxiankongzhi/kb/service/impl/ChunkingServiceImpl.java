package com.example.quanxiankongzhi.kb.service.impl;
import com.example.quanxiankongzhi.kb.entity.Chunk;
import com.example.quanxiankongzhi.kb.mapper.ChunkMapper;
import com.example.quanxiankongzhi.kb.service.ChunkingService;
import com.example.quanxiankongzhi.kb.service.EmbeddingService;
import com.example.quanxiankongzhi.kb.service.FullTextSearchService;
import com.example.quanxiankongzhi.kb.service.VectorStoreService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Slf4j
@Service
@RequiredArgsConstructor
public class ChunkingServiceImpl implements ChunkingService{
    private final ChunkMapper chunkMapper;
    private final EmbeddingService embeddingService;
    private final VectorStoreService vectorStoreService;
    private final FullTextSearchService fullTextSearchService;
    private static final int CHUNK_SIZE = 500;
    private static final int OVERLAP = 50;
    /**
     * 将文本分块并存入数据库
     */
    @Override
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
        // 如果文档重新上传，先清理旧分块
        chunkMapper.delete(new LambdaQueryWrapper<Chunk>()
                .eq(Chunk::getDocId, docId));
        fullTextSearchService.deleteByDocId(docId);
        for (Chunk chunk : chunkEntities){
            chunkMapper.insert(chunk);
            // 向量化并存入 Milvus
            List<Float> vector = embeddingService.embed(chunk.getContent());
            if (!vector.isEmpty()){
                vectorStoreService.insert(chunk.getId(), docId, kbId, chunk.getContent(), vector);
                fullTextSearchService.indexChunk(chunk.getId(), docId, kbId, chunk.getContent());
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

            String chunk = text.substring(start, end);
            // 如果当前块小于CHUNK_SIZE的一半，且不是最后一块
            if (chunk.length() < CHUNK_SIZE / 2 && chunks.size() > 0){
                // 合并到上一块
                String lastChunk = chunks.remove(chunks.size() - 1);
                chunks.add(lastChunk + " " + chunk);
            }else {
                chunks.add(chunk);
            }
            start += (CHUNK_SIZE - OVERLAP);
        }
        return chunks;
    }
}
