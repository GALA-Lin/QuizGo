package com.cupk.quizgo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("wrong_book")
public class WrongBook {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long questionId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}