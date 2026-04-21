package com.cupk.quizgo.user.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cupk.quizgo.user.entity.User;
import org.apache.ibatis.annotations.Select;

public interface UserMapper extends BaseMapper<User> {
    @Select("SELECT COUNT(*) FROM answer_record WHERE user_id=#{userId}")
    int countTotalAnswered(Long userId);
    @Select("SELECT COUNT(*) FROM answer_record WHERE user_id=#{userId} AND is_correct=1")
    int countCorrect(Long userId);
}