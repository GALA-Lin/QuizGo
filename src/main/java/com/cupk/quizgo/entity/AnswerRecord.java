package com.cupk.quizgo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("answer_record")
public class AnswerRecord {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long questionId;

    private String userAnswer;

    /**
     * 0错误 1正确
     */
    private Integer isCorrect;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}