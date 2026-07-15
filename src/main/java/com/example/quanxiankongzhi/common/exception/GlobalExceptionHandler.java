package com.example.quanxiankongzhi.common.exception;
import com.example.quanxiankongzhi.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
/**
 * 全局异常处理器
 * 统一处理应用中的异常，返回标准化的错误响应
 */
@Slf4j
@RestControllerAdvice  // 全局异常处理
public class GlobalExceptionHandler {
    /**
     * 处理权限不足异常
     */
    @ExceptionHandler(value = PermissionDeniedException.class)
    public ResponseEntity<Result<Void>> handlePermissionDeniedException(PermissionDeniedException e) {
        log.warn("权限不足: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Result.error(403, e.getMessage()));
    }
    /**
     * 处理认证失败异常
     */
    @ExceptionHandler(value = AuthenticationException.class)
    public ResponseEntity<Result<Void>> handleAuthenticationException(AuthenticationException e) {
        log.warn("认证失败: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Result.error(401, e.getMessage()));
    }
    /**
     * 处理通用运行时异常
     */
    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity<Result<Void>> handleRuntimeException(RuntimeException e) {
        log.error("运行时异常: ", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Result.error(500, "服务器内部错误"));
    }
    /**
     * 处理所有未捕获的异常
     */
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<Result<Void>> handleException(Exception e) {
        log.error("未知异常: ", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Result.error(500, "服务器内部错误"));
    }
}
