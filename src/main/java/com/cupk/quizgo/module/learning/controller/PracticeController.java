package com.cupk.quizgo.module.learning.controller;

import com.cupk.quizgo.common.result.PageResult;
import com.cupk.quizgo.common.result.Result;
import com.cupk.quizgo.module.learning.service.PracticeService;
import com.cupk.quizgo.module.learning.dto.SubmitDTO;
import com.cupk.quizgo.module.learning.vo.RecordVO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/practice")
public class PracticeController {

    @Autowired
    private PracticeService practiceService;

    /**
     * 提交答案
     * POST /api/practice/submit
     */
    @PostMapping("/submit")
    public Result<?> submit(@RequestBody SubmitDTO dto, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(practiceService.submit(userId, dto));
    }

    /**
     * 答题记录列表
     * GET /api/practice/record?page=1&size=10
     */
    @GetMapping("/record")
    public Result<PageResult<RecordVO>> record(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(practiceService.getRecord(userId, page, size));
    }
}