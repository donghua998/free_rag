package com.example.quanxiankongzhi.api.controller;
import com.example.quanxiankongzhi.api.dto.ApiKeyRequest;
import com.example.quanxiankongzhi.api.dto.ApiKeyResponse;
import com.example.quanxiankongzhi.api.dto.ApiSearchRequest;
import com.example.quanxiankongzhi.api.service.ApiKeyService;
import com.example.quanxiankongzhi.common.result.Result;
import com.example.quanxiankongzhi.kb.dto.SearchRequest;
import com.example.quanxiankongzhi.kb.dto.SearchResultVO;
import com.example.quanxiankongzhi.kb.entity.KnowledgeBase;
import com.example.quanxiankongzhi.kb.service.KnowledgeBaseService;
import com.example.quanxiankongzhi.kb.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ApiKeyController {
    private final ApiKeyService apiKeyService;
    private final SearchService searchService;
    private final KnowledgeBaseService knowledgeBaseService;
    // ====== API Key 管理（需要 JWT 认证）======
    @PostMapping("/keys/create")
    public Result<ApiKeyResponse> createKey(
            @RequestBody ApiKeyRequest request,
            @RequestAttribute("userId") Long userId
    ){
        return Result.success(apiKeyService.create(request, userId));
    }
    @GetMapping("/keys/list")
    public Result<List<ApiKeyResponse>> listKeys(@RequestAttribute("userId") Long userId){
        return Result.success(apiKeyService.listByUserId(userId));
    }
    @DeleteMapping("/keys/{id}")
    public Result<Void> revokeKey(
            @PathVariable Long id,
            @RequestAttribute("userId") Long userId
    ){
        apiKeyService.revoke(id, userId);
        return Result.success();
    }
    // ====== 外部搜索接口（需要 API Key 认证）======
    @PostMapping("/search")
    public Result<List<SearchResultVO>> search(
            @RequestBody ApiSearchRequest request,
            @RequestAttribute("userId") Long userId  // 从拦截器拿
    ) {
        // 校验该知识库是否属于当前用户
        KnowledgeBase kb = knowledgeBaseService.getById(request.getKbId());
        if (kb == null || !kb.getOwnerId().equals(userId)) {
            return Result.error(403, "无权访问该知识库");
        }
        SearchRequest req = new SearchRequest();
        req.setQuery(request.getQuery());
        req.setKbId(request.getKbId());
        req.setTopK(request.getTopK() != null ? request.getTopK() : 5);
        return Result.success(searchService.search(req));
    }
}
