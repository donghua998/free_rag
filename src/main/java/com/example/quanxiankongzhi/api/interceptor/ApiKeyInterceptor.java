package com.example.quanxiankongzhi.api.interceptor;
import com.example.quanxiankongzhi.api.entity.ApiKey;
import com.example.quanxiankongzhi.api.limiter.RateLimiter;
import com.example.quanxiankongzhi.api.service.ApiKeyService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
@Slf4j
@Component
@RequiredArgsConstructor
public class ApiKeyInterceptor implements HandlerInterceptor{
    private final ApiKeyService apiKeyService;
    private final RateLimiter rateLimiter;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler){
        // 1. 从请求头获取 API Key
        String apiKey = request.getHeader("X-API-Key");
        if (apiKey == null || apiKey.isEmpty()){
            throw new RuntimeException("缺少 API Key");
        }
        // 2. 校验 Key 是否有效
        ApiKey key = apiKeyService.validate(apiKey);
        // 3. 限流检查
        if (!rateLimiter.tryAcquire(apiKey, key.getRateLimit())){
            throw new RuntimeException("请求过于频繁，请稍后重试");
        }
        // 4. 把 Key ID 存入请求属性，供后续使用
        request.setAttribute("apiKeyId", key.getId());
        request.setAttribute("userId", key.getUserId());  // 加上这一行
        return true;
    }
}
