package com.example.quanxiankongzhi.auth.entity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
@Data
@TableName("sys_permission")

public class Permission {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String permissionCode;
    private String permissionName;
    private String resourceType;
    private String path;
    private Long parentId;
    private Integer sortOrder;
    private String icon;
    private Integer status;
    private LocalDateTime createTime;
}