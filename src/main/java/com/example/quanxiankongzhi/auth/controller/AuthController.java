package com.example.quanxiankongzhi.auth.controller;
import com.example.quanxiankongzhi.auth.dto.AuthResponse;
import com.example.quanxiankongzhi.auth.dto.LoginRequest;
import com.example.quanxiankongzhi.auth.dto.RegisterRequest;
import com.example.quanxiankongzhi.auth.service.AuthService;
import com.example.quanxiankongzhi.common.result.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<AuthResponse> register(@RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return Result.success(response);
    }
    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<AuthResponse> login(@RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return Result.success(response);
    }

}
