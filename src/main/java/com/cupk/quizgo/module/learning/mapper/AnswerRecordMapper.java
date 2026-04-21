package com.cupk.quizgo.module.learning.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cupk.quizgo.entity.AnswerRecord;
import com.cupk.quizgo.module.learning.vo.RecordVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * @since 2026/4/21 16:21
 *
 */

@Mapper
public interface AnswerRecordMapper extends BaseMapper<AnswerRecord> {

    /**
     * 分页查询答题记录，JOIN question表取题目内容
     */
    @Select("SELECT ar.id, ar.question_id, q.content AS question_content, " +
            "ar.user_answer, ar.is_correct, ar.create_time " +
            "FROM answer_record ar " +
            "LEFT JOIN question q ON ar.question_id = q.id " +
            "WHERE ar.user_id = #{userId} " +
            "ORDER BY ar.create_time DESC")
    Page<RecordVO> selectRecordPage(Page<RecordVO> page, Long userId);
}

