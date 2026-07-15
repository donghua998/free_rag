package com.example.quanxiankongzhi.auth.annotation;
import java.lang.annotation.*;
/**
 * 角色校验注解
 * 用于标记需要特定角色才能访问的方法
 *
 * 使用示例：
 * @RequireRole("ADMIN")  // 需要管理员角色
 * @GetMapping("/admin/users")
 * public Result listAllUsers(...) { }
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequireRole {
    /**
     * 需要的角色标识
     * 例如：{"ADMIN", "SUPER_ADMIN"}
     */
    String[] value();
    /**
     * 角色匹配模式
     * ANY: 拥有任意一个角色即可（默认）
     * ALL: 必须拥有所有角色
     */
    Logical logical() default Logical.ANY;
    enum Logical {
        ANY,
        ALL
    }

}
