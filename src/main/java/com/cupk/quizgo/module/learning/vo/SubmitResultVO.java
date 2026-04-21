package com.cupk.quizgo.module.learning.vo;

import lombok.Data;

/**
 * @author GALA_Lin
 */

@Data
public class SubmitResultVO {
    private Boolean isCorrect;
    private String correctAnswer;
    private String analysis;
    private Boolean addedToWrongBook;
}
