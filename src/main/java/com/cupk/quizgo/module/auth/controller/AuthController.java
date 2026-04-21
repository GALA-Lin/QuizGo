package com.cupk.quizgo.module.auth.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cupk.quizgo.module.auth.dto.AuthDTO;
import com.cupk.quizgo.module.auth.vo.AuthVO;
import com.cupk.quizgo.common.result.BusinessException;
import com.cupk.quizgo.common.result.Result;
import com.cupk.quizgo.common.result.ResultCode;
import com.cupk.quizgo.common.util.JwtUtil;
import com.cupk.quizgo.entity.User;
import com.cupk.quizgo.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;

    /** 注册 */
    @PostMapping("/register")
    public Result<Void> register(@RequestBody AuthDTO dto) {
        // 检查用户名是否已存在
        Long count = userMapper.selectCount(
                new LambdaQueryWrapper<User>().eq(User::getUsername, dto.getUsername()));
        if (count > 0) {
            throw new BusinessException(ResultCode.USER_ALREADY_EXISTS);
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(md5(dto.getPassword()));
        user.setRole(0); // 默认普通用户
        userMapper.insert(user);

        return Result.success("注册成功");
    }

    /** 登录 */
    @PostMapping("/login")
    public Result<AuthVO> login(@RequestBody AuthDTO dto) {
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getUsername, dto.getUsername()));

        if (user == null || !user.getPassword().equals(md5(dto.getPassword()))) {
            throw new BusinessException(ResultCode.INVALID_CREDENTIALS);
        }

        String token = jwtUtil.generateToken(user.getId(), user.getRole());

        AuthVO vo = new AuthVO();
        vo.setToken(token);
        vo.setUserId(user.getId());
        vo.setRole(user.getRole());
        vo.setUsername(user.getUsername());

        return Result.success(vo);
    }

    private String md5(String raw) {
        return DigestUtils.md5DigestAsHex(raw.getBytes(StandardCharsets.UTF_8));
    }
}