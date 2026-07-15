package com.example.quanxiankongzhi.user.controller;
import com.example.quanxiankongzhi.common.result.Result;
import com.example.quanxiankongzhi.user.entity.User;
import com.example.quanxiankongzhi.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.example.quanxiankongzhi.auth.annotation.RequireRole;
import java.util.List;
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserMapper userMapper;
    @GetMapping("/info")
    public Result<User> getUserInfo(@RequestAttribute("userId") Long userId) {
        User user = userMapper.selectById(userId);
        user.setPassword(null);
        return Result.success(user);
    }
    /**
     * 获取所有用户列表（需要管理员权限）
     */
    @RequireRole("ADMIN")
    @GetMapping("/list")
    public Result<List<User>> listAllUsers() {
        List<User> users = userMapper.selectList(null);
        users.forEach(user -> user.setPassword(null));
        return Result.success(users);
    }
}
