package com.cupk.quizgo.user.controller;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cupk.quizgo.common.result.Result;
import com.cupk.quizgo.user.entity.User;
import com.cupk.quizgo.user.mapper.UserMapper;
import com.cupk.quizgo.user.vo.UserInfoVO;
import com.cupk.quizgo.wrongbook.entity.WrongBook;
import com.cupk.quizgo.wrongbook.mapper.WrongBookMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserInfoController {
    private final UserMapper userMapper;
    // 跨模块依赖：直接引入 wrongbook 的 Mapper 即可，Spring 会统一管理 Bean
    private final WrongBookMapper wrongBookMapper;

    @GetMapping("/info")
    public Result<UserInfoVO> info(@RequestAttribute Long userId) {
        User user = userMapper.selectById(userId);
        UserInfoVO vo = new UserInfoVO();
        BeanUtils.copyProperties(user, vo);
        vo.setTotalAnswered(userMapper.countTotalAnswered(userId));
        vo.setCorrectCount(userMapper.countCorrect(userId));
        vo.setWrongCount(wrongBookMapper.selectCount(
            new LambdaQueryWrapper<WrongBook>().eq(WrongBook::getUserId, userId)
        ));
        return Result.success(vo);
    }
}