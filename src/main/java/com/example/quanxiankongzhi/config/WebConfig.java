package com.example.quanxiankongzhi.config;
import com.example.quanxiankongzhi.auth.interceptor.JwtInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    private final JwtInterceptor jwtInterceptor;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/**")           // 拦截所有请求
                .excludePathPatterns(              // 排除以下路径
                        "/auth/login",             // 登录
                        "/auth/register",          // 注册
                        "/error"                   // 错误页面
                );
    }
}
