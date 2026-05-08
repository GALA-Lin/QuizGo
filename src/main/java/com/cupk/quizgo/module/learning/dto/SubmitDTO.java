package com.cupk.quizgo.module.learning.dto;

import lombok.Data;

/**
 * @author GALA_Lin
 */
@Data
public class SubmitDTO {
    private Long questionId;
    private String userAnswer;
}
