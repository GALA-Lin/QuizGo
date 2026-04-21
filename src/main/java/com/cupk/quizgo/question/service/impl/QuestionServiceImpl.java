package com.cupk.quizgo.question.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cupk.quizgo.common.result.PageResult;
import com.cupk.quizgo.question.entity.Question;
import com.cupk.quizgo.question.mapper.QuestionMapper_ZZ;
import com.cupk.quizgo.question.service.IQuestionService;
import com.cupk.quizgo.question.vo.QuestionVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper_ZZ, Question> implements IQuestionService {

    @Override
    public PageResult<QuestionVO> getQuestionList(Long subjectId, Integer page, Integer size) {
        Page<Question> pageObj = new Page<>(page, size);
        LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Question::getSubjectId, subjectId);
        page(pageObj, wrapper);

        List<QuestionVO> voList = pageObj.getRecords().stream().map(q -> {
            QuestionVO vo = new QuestionVO();
            BeanUtils.copyProperties(q, vo);
            return vo;
        }).collect(Collectors.toList());

        return PageResult.of(pageObj.getTotal(), pageObj.getSize(), pageObj.getCurrent(), voList);
    }

    @Override
    public PageResult<Question> getAdminQuestionList(Long subjectId, Integer page, Integer size, String keyword) {
        Page<Question> pageObj = new Page<>(page, size);
        LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Question::getSubjectId, subjectId);
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Question::getContent, keyword);
        }
        page(pageObj, wrapper);
        return PageResult.of(pageObj);
    }

    @Override
    public void addQuestion(Question question) {
        save(question);
    }

    @Override
    public void updateQuestion(Question question) {
        updateById(question);
    }

    @Override
    public void deleteQuestion(Long id) {
        removeById(id);
    }
}