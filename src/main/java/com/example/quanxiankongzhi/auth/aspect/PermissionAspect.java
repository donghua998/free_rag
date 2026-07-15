package com.example.quanxiankongzhi.auth.aspect;
import com.example.quanxiankongzhi.auth.annotation.RequirePermission;
import com.example.quanxiankongzhi.auth.annotation.RequireRole;
import com.example.quanxiankongzhi.auth.service.PermissionService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import com.example.quanxiankongzhi.common.exception.AuthenticationException;
import com.example.quanxiankongzhi.common.exception.PermissionDeniedException;
import java.util.Arrays;
import java.util.Set;
/**
 * 权限校验切面
 * 拦截带有 @RequirePermission 和 @RequireRole 注解的方法
 * 进行权限和角色校验
 */
@Slf4j
@Aspect                     // 声明为切面
@Component                  // 注册为 Spring Bean
@RequiredArgsConstructor    // 构造器注入
public class PermissionAspect {
    private final PermissionService permissionService;
    /**
     * 拦截带有 @RequirePermission 注解的方法
     * 校验用户是否有指定权限
     *
     * @param joinPoint 连接点，可以获取方法信息
     * @param requirePermission 权限注解
     * @return 方法执行结果
     * @throws Throwable 校验失败时抛出异常
     */
    @Around("@annotation(requirePermission)")  // 拦截带有 @RequirePermission 的方法
    public Object checkPermission(ProceedingJoinPoint joinPoint, RequirePermission requirePermission) throws Throwable{
        // 1. 获取当前登录用户ID
        Long userId = getCurrentUserId();
        if (userId == null) {
            throw new AuthenticationException("未登录或登录已过期");
        }
        // 2. 获取需要的权限
        String[] requiredPermissions = requirePermission.value();
        RequirePermission.Logical logical = requirePermission.logical();
        // 3. 校验权限
        boolean hasPermission;
        if (logical == RequirePermission.Logical.ALL){
            // 必须拥有所有权限
            hasPermission = Arrays.stream(requiredPermissions)
                    .allMatch(perm -> permissionService.hasPermission(userId, perm));
        }else{
            // 拥有任意一个权限即可
            hasPermission = Arrays.stream(requiredPermissions)
                    .anyMatch(perm -> permissionService.hasPermission(userId, perm));
        }
        if (!hasPermission){
            log.warn("用户 [{}] 缺少权限: {}", userId, Arrays.toString(requiredPermissions));
            throw new PermissionDeniedException("权限不足，无法访问该资源");
        }
        // 5. 校验通过，执行原方法
        return joinPoint.proceed();
    }
    /**
     * 拦截带有 @RequireRole 注解的方法
     * 校验用户是否有指定角色
     */
    @Around("@annotation(requireRole)")
    public Object checkRole(ProceedingJoinPoint joinPoint, RequireRole requireRole) throws Throwable{
        // 1. 获取当前登录用户ID
        Long userId = getCurrentUserId();
        if (userId == null) {
            throw new AuthenticationException("未登录或登录已过期");
        }
        // 2. 获取需要的角色
        String[] requiredRoles = requireRole.value();
        RequireRole.Logical logical = requireRole.logical();
        // 3. 校验角色
        boolean hasRole;
        if (logical == RequireRole.Logical.ALL){
            // 必须拥有所有角色
            hasRole = Arrays.stream(requiredRoles)
                    .allMatch(role -> permissionService.hasRole(userId, role));
        }else{
            // 拥有任意一个角色即可
            hasRole = Arrays.stream(requiredRoles)
                    .anyMatch(role -> permissionService.hasRole(userId, role));
        }
        // 4. 校验失败抛出异常
        if (!hasRole) {
            log.warn("用户 [{}] 缺少角色: {}", userId, Arrays.toString(requiredRoles));
            throw new PermissionDeniedException("权限不足，需要特定角色");
        }
        // 5. 校验通过，执行原方法
        return joinPoint.proceed();
    }
    /**
     * 从请求属性中获取当前用户ID
     * 用户ID在 JwtInterceptor 中解析 Token 后设置
     *
     * @return 用户ID，未登录返回 null
     */
    private Long getCurrentUserId(){
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return null;
        }
        HttpServletRequest request = attributes.getRequest();
        Object userId = request.getAttribute("userId");
        return userId != null ? (Long) userId : null;
    }
}
