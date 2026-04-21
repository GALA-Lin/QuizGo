package com.cupk.quizgo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

@Data
@TableName("question")
public class Question {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long subjectId;

    private String content;

    /**
     * 0单选 1多选 2判断
     */
    private Integer type;

    /**
     * 选项JSON数组，建议在业务层使用 JSON 工具类解析
     */
    private String optionsJson;

    private String answer;

    private String analysis;

    /**
     * 1简单 2中等 3困难
     */
    private Integer difficulty;
}