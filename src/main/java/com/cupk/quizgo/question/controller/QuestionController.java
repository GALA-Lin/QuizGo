package com.cupk.quizgo.question.controller;

import com.alibaba.excel.EasyExcel;
import com.cupk.quizgo.common.result.PageResult;
import com.cupk.quizgo.common.result.Result;
import com.cupk.quizgo.question.dto.QuestionExcelDTO;
import com.cupk.quizgo.question.entity.Question;
import com.cupk.quizgo.question.listener.QuestionExcelListener;
import com.cupk.quizgo.question.service.IQuestionService;
import com.cupk.quizgo.question.vo.QuestionVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class QuestionController {

    private final IQuestionService questionService;

    /**
     * 前台：获取题目列表（按科目）
     */
    @GetMapping("/question/list")
    public Result<PageResult<QuestionVO>> getQuestionList(@RequestParam Long subjectId,
                                                          @RequestParam(defaultValue = "1") Integer page,
                                                          @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(questionService.getQuestionList(subjectId, page, size));
    }

    /**
     * 后台：获取题目列表（支持按科目、题库、关键词筛选）
     */
    @GetMapping("/admin/question/list")
    public Result<PageResult<Question>> getAdminQuestionList(@RequestParam Long subjectId,
                                                             @RequestParam(required = false) Long bankId,
                                                             @RequestParam(defaultValue = "1") Integer page,
                                                             @RequestParam(defaultValue = "10") Integer size,
                                                             @RequestParam(required = false) String keyword) {
        return Result.success(questionService.getAdminQuestionList(subjectId, bankId, page, size, keyword));
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

    /**
     * 后台：批量导入 Excel 题目
     */
    @PostMapping("/admin/question/import")
    public Result<Void> importQuestions(@RequestParam("file") MultipartFile file,
                                        @RequestParam("bankId") Long bankId,
                                        @RequestParam("subjectId") Long subjectId) {
        try {
            // 使用 EasyExcel 读取文件流，并通过监听器异步入库
            EasyExcel.read(file.getInputStream(),
                            QuestionExcelDTO.class,
                            new QuestionExcelListener(questionService, bankId, subjectId))
                    .sheet()
                    .doRead();

            return Result.success("题目批量导入成功！");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("题目导入失败: " + e.getMessage());
        }
    }
}