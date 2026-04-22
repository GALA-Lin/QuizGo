package com.cupk.quizgo.module.learning.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cupk.quizgo.common.result.PageResult;
import com.cupk.quizgo.module.learning.dto.SubmitDTO;
import com.cupk.quizgo.entity.AnswerRecord;
import com.cupk.quizgo.entity.Question;
import com.cupk.quizgo.entity.WrongBook;
import com.cupk.quizgo.module.learning.mapper.AnswerRecordMapper;
import com.cupk.quizgo.module.learning.mapper.QuestionMapper_hls;
import com.cupk.quizgo.module.learning.mapper.WrongBookMapper_hls;
import com.cupk.quizgo.module.learning.vo.RecordVO;
import com.cupk.quizgo.module.learning.vo.SubmitResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.stream.Collectors;


@Service
public class PracticeService {

    @Autowired
    private QuestionMapper_hls questionMapper;

    @Autowired
    private AnswerRecordMapper answerRecordMapper;

    @Autowired
    private WrongBookMapper_hls wrongBookMapper;

    /**
     * 提交答案核心逻辑：
     * 1. 查题目
     * 2. 判断对错（兼容单选/多选/判断）
     * 3. 插入答题记录
     * 4. 答错则插入错题本（重复忽略）
     */
    public SubmitResultVO submit(Long userId, SubmitDTO dto) {
        Question question = questionMapper.selectById(dto.getQuestionId());
        if (question == null) {
            throw new RuntimeException("题目不存在");
        }

        // 兼容多选：将选项拆分排序后再比较，避免顺序不同判错
        boolean isCorrect = checkAnswer(question.getAnswer(), dto.getUserAnswer());

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
     * 答案比较：兼容单选（"A"）和多选（"A,B,C,D"）
     * 将选项拆分 → 去空格 → 转大写 → 排序 → 拼接后做字符串比较
     * 例：用户答 "D,A,B,C" 与正确答案 "A,B,C,D" 视为正确
     */
    private boolean checkAnswer(String correct, String userAnswer) {
        if (correct == null || userAnswer == null) return false;
        return normalize(correct).equals(normalize(userAnswer));
    }

    private String normalize(String answer) {
        return Arrays.stream(answer.split(","))
                .map(String::trim)
                .map(String::toUpperCase)
                .sorted()
                .collect(Collectors.joining(","));
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