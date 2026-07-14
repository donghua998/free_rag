package com.example.quanxiankongzhi.auth.service;
import com.example.quanxiankongzhi.auth.dto.AuthResponse;
import com.example.quanxiankongzhi.auth.dto.LoginRequest;
import com.example.quanxiankongzhi.auth.dto.RegisterRequest;

public interface AuthService {
    /**
     * 用户注册
     */
    AuthResponse register(RegisterRequest request);
    /**
     * 用户登录
     */
    AuthResponse login(LoginRequest request);
}
