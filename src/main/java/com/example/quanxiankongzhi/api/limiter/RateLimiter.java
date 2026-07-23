package com.example.quanxiankongzhi.api.limiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
@Slf4j
@Component
public class RateLimiter {
    // API Key → 请求时间戳队列
    private final Map<String, Deque<Long>> cache = new ConcurrentHashMap<>();
    public boolean tryAcquire(String key, int rateLimit){
        long now = System.currentTimeMillis();
        Deque<Long> deque = cache.computeIfAbsent(key, k -> new LinkedList<>());
        // 移除1分钟前的记录
        while (!deque.isEmpty() && deque.peekFirst() < now - 60_000){
            deque.pollFirst();
        }
        if (deque.size() >= rateLimit){
            log.warn("限流触发, key: {}, 当前请求数: {}, 限制: {}", key, deque.size(), rateLimit);
            return false;
        }
        deque.addLast(now);
        return true;
    }

}
