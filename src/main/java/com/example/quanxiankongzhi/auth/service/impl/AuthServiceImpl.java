package com.example.quanxiankongzhi.auth.service.impl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.quanxiankongzhi.auth.dto.AuthResponse;
import com.example.quanxiankongzhi.auth.dto.LoginRequest;
import com.example.quanxiankongzhi.auth.dto.RegisterRequest;
import com.example.quanxiankongzhi.auth.service.AuthService;
import com.example.quanxiankongzhi.common.util.JwtUtil;
import com.example.quanxiankongzhi.user.entity.User;
import com.example.quanxiankongzhi.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserMapper userMapper;
    @Override
    public AuthResponse register(RegisterRequest request) {
        // 检查用户名是否已存在
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, request.getUsername());
        if (userMapper.selectOne(wrapper) != null) {
            throw new RuntimeException("用户名已存在");
        }
        // 创建用户
        User user = new User();
        user.setUsername(request.getUsername());
        // 密码MD5加密
        user.setPassword(DigestUtils.md5DigestAsHex(request.getPassword().getBytes(StandardCharsets.UTF_8)));
        user.setEmail(request.getEmail());
        user.setStatus(1);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        userMapper.insert(user);
        // 生成Token
        String token = JwtUtil.generateToken(user.getId(), user.getUsername());

        AuthResponse response = new AuthResponse();
        response.setToken(token);
        response.setUserId(user.getId());
        response.setUsername(user.getUsername());
        response.setAvatar(user.getAvatar());
        return response;
    }
    @Override
    public AuthResponse login(LoginRequest request){
        // 查询用户
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, request.getUsername());
        User user = userMapper.selectOne(wrapper);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        // 校验密码
        String encryptedPassword = DigestUtils.md5DigestAsHex(request.getPassword().getBytes(StandardCharsets.UTF_8));
        if (!encryptedPassword.equals(user.getPassword())) {
            throw new RuntimeException("密码错误");
        }

        // 检查状态
        if (user.getStatus() != 1) {
            throw new RuntimeException("账号已被禁用");
        }
        // 生成Token
        String token = JwtUtil.generateToken(user.getId(), user.getUsername());

        AuthResponse response = new AuthResponse();
        response.setToken(token);
        response.setUserId(user.getId());
        response.setUsername(user.getUsername());
        response.setAvatar(user.getAvatar());
        return response;
    }
}
