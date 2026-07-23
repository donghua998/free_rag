package com.example.quanxiankongzhi.api.service;
import com.example.quanxiankongzhi.api.dto.ApiKeyRequest;
import com.example.quanxiankongzhi.api.dto.ApiKeyResponse;
import com.example.quanxiankongzhi.api.entity.ApiKey;

import java.util.List;
public interface ApiKeyService{
    ApiKeyResponse create(ApiKeyRequest request, Long userId);
    List<ApiKeyResponse> listByUserId(Long userId);
    void revoke(Long id, Long userId);               // 禁用
    ApiKey validate(String apiKey);                  // 校验（拦截器用）
}
