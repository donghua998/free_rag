package com.example.quanxiankongzhi.api.service.Impl;
import com.example.quanxiankongzhi.api.dto.ApiKeyRequest;
import com.example.quanxiankongzhi.api.dto.ApiKeyResponse;
import com.example.quanxiankongzhi.api.entity.ApiKey;
import com.example.quanxiankongzhi.api.mapper.ApiKeyMapper;
import com.example.quanxiankongzhi.api.service.ApiKeyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
@Slf4j
@Service
@RequiredArgsConstructor
public class ApiKeyServiceImpl implements ApiKeyService {
    private final ApiKeyMapper apiKeyMapper;
    @Override
    public ApiKeyResponse create(ApiKeyRequest request, Long userId){
        String key = UUID.randomUUID().toString().replace("-", "");
        ApiKey entity = new ApiKey();
        entity.setApiKey(key);
        entity.setName(request.getName());
        entity.setUserId(userId);
        entity.setStatus(1);
        entity.setRateLimit(request.getRateLimit() != null ? request.getRateLimit() : 30);
        entity.setCreateTime(LocalDateTime.now());
        apiKeyMapper.insert(entity);

        return toResponse(entity);
    }
    @Override
    public List<ApiKeyResponse> listByUserId(Long userId) {
        return apiKeyMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ApiKey>()
                        .eq(ApiKey::getUserId, userId)
        ).stream().map(this::toResponse).collect(Collectors.toList());
    }
    @Override
    public void revoke(Long id, Long userId){
        ApiKey key = apiKeyMapper.selectById(id);
        if (key == null || !key.getUserId().equals(userId)){
            throw new RuntimeException("API Key 不存在");
        }
        key.setStatus(0);
        apiKeyMapper.updateById(key);
    }
    @Override
    public ApiKey validate(String apiKey){
        ApiKey key = apiKeyMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ApiKey>()
                        .eq(ApiKey::getApiKey, apiKey)
        );
        if (key == null){
            throw new RuntimeException("API Key 无效");
        }
        if (key.getStatus() != 1){
            throw new RuntimeException("API Key 已被禁用");
        }
        if (key.getExpireTime() != null && key.getExpireTime().isBefore(LocalDateTime.now())){
            throw new RuntimeException("API Key 已过期");
        }
        return key;
    }
    private ApiKeyResponse toResponse(ApiKey entity){
        ApiKeyResponse resp = new ApiKeyResponse();
        resp.setId(entity.getId());
        resp.setApiKey(entity.getApiKey());
        resp.setName(entity.getName());
        resp.setStatus(entity.getStatus());
        resp.setRateLimit(entity.getRateLimit());
        resp.setCreateTime(entity.getCreateTime());
        resp.setExpireTime(entity.getExpireTime());
        return resp;
    }
}
