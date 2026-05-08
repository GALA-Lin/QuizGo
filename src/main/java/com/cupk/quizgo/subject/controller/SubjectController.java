package com.cupk.quizgo.subject.controller;

import com.cupk.quizgo.common.result.Result;
import com.cupk.quizgo.entity.Subject;
import com.cupk.quizgo.subject.service.ISubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/subject")
public class SubjectController {

    @Autowired
    private ISubjectService subjectService;

    /**
     * 获取所有科目分类列表
     */
    @GetMapping("/list")
    public Result<List<Subject>> list() {
        // mybatis-plus 提供的 list() 方法会查询表中所有数据
        List<Subject> subjectList = subjectService.list();
        return Result.success(subjectList);
    }
}