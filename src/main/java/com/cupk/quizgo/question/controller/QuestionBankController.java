package com.cupk.quizgo.question.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cupk.quizgo.common.result.Result;
import com.cupk.quizgo.entity.QuestionBank;
import com.cupk.quizgo.question.entity.Question;
import com.cupk.quizgo.question.mapper.QuestionBankMapper;
import com.cupk.quizgo.question.mapper.QuestionMapper_ZZ;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/bank")
@RequiredArgsConstructor
public class QuestionBankController {

    private final QuestionBankMapper questionBankMapper;
    private final QuestionMapper_ZZ questionMapper; // 注入题目 Mapper 用于级联删除

    /**
     * 获取题库列表
     */
    @GetMapping("/list")
    public Result<List<QuestionBank>> getBankList(@RequestParam Long subjectId) {
        LambdaQueryWrapper<QuestionBank> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(QuestionBank::getSubjectId, subjectId)
                .orderByDesc(QuestionBank::getId);
        return Result.success(questionBankMapper.selectList(wrapper));
    }

    /**
     * 新建题库
     */
    @PostMapping("/add")
    public Result<Void> addBank(@RequestBody QuestionBank bank) {
        questionBankMapper.insert(bank);
        return Result.success("题库创建成功");
    }

    /**
     * 删除题库（及其下属所有题目）
     * 使用 @Transactional 保证两步操作要么同时成功，要么同时失败
     */
    @DeleteMapping("/{id}")
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> deleteBank(@PathVariable Long id) {
        // 1. 先删除该题库下的所有题目
        LambdaQueryWrapper<Question> qWrapper = new LambdaQueryWrapper<>();
        qWrapper.eq(Question::getBankId, id);
        questionMapper.delete(qWrapper);

        // 2. 再删除题库本身
        questionBankMapper.deleteById(id);

        return Result.success("题库及其所属题目已全部删除");
    }
}