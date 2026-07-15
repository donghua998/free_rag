package com.example.quanxiankongzhi.auth.annotation;
import java.lang.annotation.*;
/**
 * 权限校验注解
 * 用于标记需要特定权限才能访问的方法
 *
 * 使用示例：
 * @RequirePermission("kb:add")  // 需要 kb:add 权限
 * @PostMapping("/kb/create")
 * public Result createKnowledgeBase(...) { }
 *
 * @RequirePermission({"kb:add", "kb:edit"})  // 需要多个权限中的任意一个
 * @PostMapping("/kb/save")
 * public Result saveKnowledgeBase(...) { }
 */
@Target(ElementType.METHOD)           // 注解用在方法上
@Retention(RetentionPolicy.RUNTIME)   // 运行时保留
@Documented                           // 生成文档时包含
public @interface RequirePermission {
    /**
     * 需要的权限标识
     * 可以指定一个或多个权限，用户拥有其中任意一个即可通过
     * 例如：{"kb:add", "kb:edit"}
     */
    String[] value();
    /**
     * 权限匹配模式
     * ANY: 拥有任意一个权限即可（默认）
     * ALL: 必须拥有所有权限
     */
    Logical logical() default Logical.ANY;
    /**
     * 逻辑模式枚举
     */
    enum Logical {
        /**
         * 任意一个权限满足即可
         */
        ANY,
        /**
         * 必须满足所有权限
         */
        ALL
    }
}
