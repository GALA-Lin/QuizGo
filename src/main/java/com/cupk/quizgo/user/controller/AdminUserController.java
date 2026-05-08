package com.cupk.quizgo.user.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cupk.quizgo.common.result.PageResult;
import com.cupk.quizgo.common.result.Result;
import com.cupk.quizgo.entity.User;
import com.cupk.quizgo.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 管理后台用户接口 - 由 B 模块负责
 */
@RestController
@RequestMapping("/api/admin/user")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserMapper userMapper;

    @GetMapping("/list")
    public Result<PageResult<User>> getUserList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<User> pageParam = new Page<>(page, size);
        userMapper.selectPage(pageParam, null);
        return Result.success(PageResult.of(pageParam));
    }
}