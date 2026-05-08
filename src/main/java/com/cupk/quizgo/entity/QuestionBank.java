package com.cupk.quizgo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 题库实体类
 */
@Data
@TableName("question_bank")
public class QuestionBank {

    // 主键ID，MyBatis-Plus 自动处理自增
    @TableId(type = IdType.AUTO)
    private Long id;

    // 所属科目ID
    private Long subjectId;

    // 题库名称
    private String name;

    // 题库简介
    private String description;

    // 封面图URL
    private String coverImg;

    // 创建时间
    private LocalDateTime createTime;
}