package com.example.quanxiankongzhi.auth.interceptor;
import com.example.quanxiankongzhi.common.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
@Slf4j
@Component
public class JwtInterceptor implements HandlerInterceptor{
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 获取请求头中的Token
        String token = request.getHeader("Authorization");

        if (token == null || !token.startsWith("Bearer ")) {
            throw new RuntimeException("缺少Token或格式错误");
        }
        // 去掉 Bearer 前缀
        token = token.substring(7);
        // 验证Token
        if (!JwtUtil.validateToken(token)) {
            throw new RuntimeException("Token无效或已过期");
        }
        // 将用户信息放入请求属性（可选）
        Long userId = JwtUtil.getUserId(token);
        String username = JwtUtil.getUsername(token);
        request.setAttribute("userId", userId);
        request.setAttribute("username", username);
        return true;
    }
}
