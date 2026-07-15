package com.example.quanxiankongzhi.auth.service.impl;
import com.example.quanxiankongzhi.auth.entity.Permission;
import com.example.quanxiankongzhi.auth.entity.Role;
import com.example.quanxiankongzhi.auth.mapper.PermissionMapper;
import com.example.quanxiankongzhi.auth.mapper.RoleMapper;
import com.example.quanxiankongzhi.auth.service.PermissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
/**
 * 权限服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {
    private final RoleMapper roleMapper;
    private final PermissionMapper permissionMapper;
    /**
     * 查询用户的所有角色
     */
    @Override

    public List<Role> getUserRoles(Long userId) {
        return roleMapper.selectRolesByUserId(userId);
    }
    /**
     * 查询用户的所有权限
     */
    @Override
    public List<Permission> getUserPermissions(Long userId) {
        return permissionMapper.selectPermissionsByUserId(userId);
    }
    /**
     * 判断用户是否有指定权限
     */
    @Override
    public boolean hasPermission(Long userId, String permissionCode) {
        // 获取用户所有权限标识
        Set<String> permissions = getUserPermissionCodes(userId);
        // 判断是否包含指定权限
        return permissions.contains(permissionCode);
    }
    /**
     * 判断用户是否有指定角色
     */
    @Override
    public boolean hasRole(Long userId, String roleCode) {
        // 查询用户角色
        List<Role> roles = getUserRoles(userId);
        // 检查是否包含指定角色
        return roles.stream()
                .anyMatch(role -> role.getRoleCode().equals(roleCode));
    }
    /**
     * 获取用户所有权限标识
     */
    @Override
    public Set<String> getUserPermissionCodes(Long userId) {
        List<Permission> permissions = getUserPermissions(userId);
        // 提取权限标识并去重
        return permissions.stream()
                .map(Permission::getPermissionCode)
                .collect(Collectors.toSet());
    }

}
