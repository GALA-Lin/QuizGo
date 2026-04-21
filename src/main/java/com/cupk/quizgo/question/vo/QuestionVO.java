package com.cupk.quizgo.question.vo;

import lombok.Data;

@Data
public class QuestionVO {
    private Long id;
    private Long subjectId;
    private String content;
    private Integer type;
    private String optionsJson;
    private Integer difficulty;
}