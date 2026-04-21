package com.cupk.quizgo.module.learning.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author GALA_Lin
 */

@Data
public class RecordVO {
    private Long id;
    private Long questionId;
    private String questionContent;  // JOIN question表得到
    private String userAnswer;
    private Integer isCorrect;
    private LocalDateTime createTime;
}
