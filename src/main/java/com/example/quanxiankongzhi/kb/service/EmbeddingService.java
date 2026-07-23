package com.example.quanxiankongzhi.kb.service;
import java.util.List;
public interface EmbeddingService{
    List<Float> embed(String text);
}