package com.cupk.quizgo.module.auth.vo;

import lombok.Data;

@Data
public class AuthVO {
    private String token;
    private Long userId;
    private Integer role;
    private String username;
}