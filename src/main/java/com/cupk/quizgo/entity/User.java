package com.cupk.quizgo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("user")
public class User {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;

    private String password; // md5

    private String avatar;

    /**
     * 0普通用户 1管理员
     */
    private Integer role;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}