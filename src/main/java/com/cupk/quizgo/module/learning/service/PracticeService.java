package com.cupk.quizgo.module.learning.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cupk.quizgo.common.result.PageResult;
import com.cupk.quizgo.module.learning.dto.SubmitDTO;
import com.cupk.quizgo.entity.AnswerRecord;
import com.cupk.quizgo.entity.Question;
import com.cupk.quizgo.entity.WrongBook;
import com.cupk.quizgo.module.learning.mapper.AnswerRecordMapper;
import com.cupk.quizgo.module.learning.mapper.QuestionMapper;
import com.cupk.quizgo.module.learning.mapper.WrongBookMapper;
import com.cupk.quizgo.module.learning.vo.RecordVO;
import com.cupk.quizgo.module.learning.vo.SubmitResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class PracticeService {

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private AnswerRecordMapper answerRecordMapper;

    @Autowired
    private WrongBookMapper wrongBookMapper;

    /**
     * 提交答案核心逻辑：
     * 1. 查题目
     * 2. 判断对错
     * 3. 插入答题记录
     * 4. 答错则插入错题本（重复忽略）
     */
    public SubmitResultVO submit(Long userId, SubmitDTO dto) {
        Question question = questionMapper.selectById(dto.getQuestionId());
        if (question == null) {
            throw new RuntimeException("题目不存在");
        }

        boolean isCorrect = question.getAnswer().equalsIgnoreCase(dto.getUserAnswer());

        AnswerRecord record = new AnswerRecord();
        record.setUserId(userId);
        record.setQuestionId(dto.getQuestionId());
        record.setUserAnswer(dto.getUserAnswer());
        record.setIsCorrect(isCorrect ? 1 : 0);
        answerRecordMapper.insert(record);

        boolean addedToWrongBook = false;
        if (!isCorrect) {
            WrongBook wrongBook = new WrongBook();
            wrongBook.setUserId(userId);
            wrongBook.setQuestionId(dto.getQuestionId());
            int inserted = wrongBookMapper.insertIgnore(wrongBook);
            addedToWrongBook = inserted > 0;
        }

        SubmitResultVO vo = new SubmitResultVO();
        vo.setIsCorrect(isCorrect);
        vo.setCorrectAnswer(question.getAnswer());
        vo.setAnalysis(question.getAnalysis());
        vo.setAddedToWrongBook(addedToWrongBook);
        return vo;
    }

    /**
     * 分页查询答题记录，直接用 PageResult.of(IPage) 包装
     */
    public PageResult<RecordVO> getRecord(Long userId, Integer page, Integer size) {
        Page<RecordVO> pageParam = new Page<>(page, size);
        Page<RecordVO> result = answerRecordMapper.selectRecordPage(pageParam, userId);
        return PageResult.of(result);
    }
}