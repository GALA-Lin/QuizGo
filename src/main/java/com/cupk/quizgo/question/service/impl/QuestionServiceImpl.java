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

        // 转换 VO
        List<QuestionVO> list = pageObj.getRecords().stream().map(q -> {
            QuestionVO vo = new QuestionVO();
            BeanUtils.copyProperties(q, vo);
            return vo;
        }).collect(Collectors.toList());

        // 适配你项目 PageResult.of 的四个参数：total, size, current, records
        return PageResult.of(pageObj.getTotal(), pageObj.getSize(), pageObj.getCurrent(), list);
    }

    @Override
    public PageResult<Question> getAdminQuestionList(Long subjectId, Long bankId, Integer page, Integer size, String keyword) {
        Page<Question> pageObj = new Page<>(page, size);
        LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<>();

        wrapper.eq(Question::getSubjectId, subjectId);

        if (bankId != null) {
            wrapper.eq(Question::getBankId, bankId);
        }

        if (StringUtils.hasText(keyword)) {
            wrapper.like(Question::getContent, keyword);
        }

        wrapper.orderByDesc(Question::getId);

        page(pageObj, wrapper);

        // 同样适配四个参数：total, size, current, records
        return PageResult.of(pageObj.getTotal(), pageObj.getSize(), pageObj.getCurrent(), pageObj.getRecords());
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