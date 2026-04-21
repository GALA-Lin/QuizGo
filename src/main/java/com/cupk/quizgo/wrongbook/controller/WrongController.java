package com.cupk.quizgo.wrongbook.controller;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cupk.quizgo.common.result.Result;
import com.cupk.quizgo.wrongbook.entity.WrongBook;
import com.cupk.quizgo.wrongbook.mapper.WrongBookMapper;
import com.cupk.quizgo.wrongbook.vo.WrongBookVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wrong")
@RequiredArgsConstructor
public class WrongController {
    private final WrongBookMapper wrongBookMapper;

    @GetMapping("/list")
    public Result<Page<WrongBookVO>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestAttribute Long userId) {
        return Result.success(wrongBookMapper.selectWrongPage(new Page<>(page, size), userId));
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id, @RequestAttribute Long userId) {
        int count = wrongBookMapper.delete(
            new LambdaQueryWrapper<WrongBook>()
                .eq(WrongBook::getId, id)
                .eq(WrongBook::getUserId, userId)
        );
        return count > 0 ? Result.success(null) : Result.fail("删除失败或无权限");
    }
}