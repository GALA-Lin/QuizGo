package com.cupk.quizgo.module.learning.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cupk.quizgo.entity.Question;
import org.apache.ibatis.annotations.Mapper;

/**
 * @since 2026/4/21 16:36
 *
 */

@Mapper
public interface QuestionMapper extends BaseMapper<Question> {
    // BaseMapper 已提供 selectById，无需额外写方法
}