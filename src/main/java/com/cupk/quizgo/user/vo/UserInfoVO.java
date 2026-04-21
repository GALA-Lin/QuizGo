package com.cupk.quizgo.user.vo;
import lombok.Data;

@Data
public class UserInfoVO {
    private Long id, role;
    private String username, avatar, createTime;
    private Integer totalAnswered, correctCount, wrongCount;
}