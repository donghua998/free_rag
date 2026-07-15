package com.example.quanxiankongzhi.auth.service;
import com.example.quanxiankongzhi.auth.entity.Permission;
import com.example.quanxiankongzhi.auth.entity.Role;

import java.util.List;
import java.util.Set;
/**
 * 权限服务接口
 * 提供用户角色和权限的查询功能
 */

public interface PermissionService {
    /**
     * 根据用户ID查询用户拥有的所有角色
     * @param userId 用户ID
     * @return 角色列表
     */
    List<Role> getUserRoles(Long userId);
    /**
     * 根据用户ID查询用户拥有的所有权限
     * @param userId 用户ID
     * @return 权限列表
     */
    List<Permission> getUserPermissions(Long userId);
    /**
     * 判断用户是否有指定权限
     * @param userId 用户ID
     * @param permissionCode 权限标识，如 "kb:add"
     * @return true-有权限，false-无权限
     */
    boolean hasPermission(Long userId, String permissionCode);
    /**
     * 判断用户是否有指定角色
     * @param userId 用户ID
     * @param roleCode 角色标识，如 "ADMIN"
     * @return true-有角色，false-无角色
     */
    boolean hasRole(Long userId, String roleCode);
    /**
     * 获取用户所有权限标识（用于缓存）
     * @param userId 用户ID
     * @return 权限标识集合
     */
    Set<String> getUserPermissionCodes(Long userId);
}
