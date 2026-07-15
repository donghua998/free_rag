package com.example.quanxiankongzhi.common.exception;
/**
 * 认证异常
 * 当用户未登录或登录已过期时抛出
 */
public class AuthenticationException extends RuntimeException{
    public AuthenticationException(String message) {
        super(message);
    }
    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
