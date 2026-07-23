package com.example.quanxiankongzhi.kb.service;
public interface ChunkingService{
    int chunkAndSave(String text, Long docId, Long kbId);
}
