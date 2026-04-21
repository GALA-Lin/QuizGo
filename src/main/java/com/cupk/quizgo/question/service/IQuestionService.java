package com.cupk.quizgo.question.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cupk.quizgo.common.result.PageResult;
import com.cupk.quizgo.question.entity.Question;
import com.cupk.quizgo.question.vo.QuestionVO;

public interface IQuestionService extends IService<Question> {

    PageResult<QuestionVO> getQuestionList(Long subjectId, Integer page, Integer size);

    PageResult<Question> getAdminQuestionList(Long subjectId, Integer page, Integer size, String keyword);

    void addQuestion(Question question);

    void updateQuestion(Question question);

    void deleteQuestion(Long id);
}