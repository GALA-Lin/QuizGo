package com.cupk.quizgo.wrongbook.vo;
import lombok.Data;

@Data
public class WrongBookVO {
    private Long wrongId, questionId;
    private String content, optionsJson, answer, analysis, subjectName, addTime;
    private Integer type, difficulty;
}