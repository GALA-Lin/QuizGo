package com.cupk.quizgo.module.learning.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cupk.quizgo.entity.WrongBook;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

/**
 * @since 2026/4/21 16:24
 *
 */
@Mapper
public interface WrongBookMapper extends BaseMapper<WrongBook> {

    /**
     * INSERT IGNORE 利用数据库唯一键，重复插入自动忽略
     * 返回影响行数：1=插入成功，0=已存在跳过
     */
    @Insert("INSERT IGNORE INTO wrong_book(user_id, question_id, create_time) " +
            "VALUES(#{userId}, #{questionId}, NOW())")
    int insertIgnore(WrongBook wrongBook);
}
