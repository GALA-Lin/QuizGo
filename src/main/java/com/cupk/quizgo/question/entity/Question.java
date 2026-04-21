package com.cupk.quizgo.question.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("question")
public class Question {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long subjectId; // 科目id
    private String content; // 题目内容
    private Integer type; // 0单选 1多选 2判断
    private String optionsJson; // 选项JSON数组
    private String answer; // 正确答案
    private String analysis; // 解析
    private Integer difficulty; // 1简单 2中等 3困难
}