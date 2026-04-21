package com.cupk.quizgo.question.controller;

import com.cupk.quizgo.common.result.PageResult;
import com.cupk.quizgo.common.result.Result;
import com.cupk.quizgo.question.entity.Question;
import com.cupk.quizgo.question.service.IQuestionService;
import com.cupk.quizgo.question.vo.QuestionVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class QuestionController {

    private final IQuestionService questionService;

    @GetMapping("/question/list")
    public Result<PageResult<QuestionVO>> getQuestionList(@RequestParam Long subjectId,
                                                          @RequestParam(defaultValue = "1") Integer page,
                                                          @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(questionService.getQuestionList(subjectId, page, size));
    }

    @GetMapping("/admin/question/list")
    public Result<PageResult<Question>> getAdminQuestionList(@RequestParam Long subjectId,
                                                             @RequestParam(defaultValue = "1") Integer page,
                                                             @RequestParam(defaultValue = "10") Integer size,
                                                             @RequestParam(required = false) String keyword) {
        return Result.success(questionService.getAdminQuestionList(subjectId, page, size, keyword));
    }

    @PostMapping("/admin/question/add")
    public Result<Void> addQuestion(@RequestBody Question question) {
        questionService.addQuestion(question);
        return Result.success("新增成功");
    }

    @PutMapping("/admin/question/update")
    public Result<Void> updateQuestion(@RequestBody Question question) {
        questionService.updateQuestion(question);
        return Result.success("修改成功");
    }

    @DeleteMapping("/admin/question/{id}")
    public Result<Void> deleteQuestion(@PathVariable Long id) {
        questionService.deleteQuestion(id);
        return Result.success("删除成功");
    }
}