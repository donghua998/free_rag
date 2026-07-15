package com.example.quanxiankongzhi.common.exception;
/**
 * 权限拒绝异常
 * 当用户没有足够权限访问资源时抛出
 */
public class PermissionDeniedException extends RuntimeException {
    public PermissionDeniedException(String message) {
        super(message);
    }
    public PermissionDeniedException(String message, Throwable cause) {
        super(message, cause);
    }
}
