package com.cupk.quizgo.wrongbook.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cupk.quizgo.wrongbook.entity.WrongBook;
import com.cupk.quizgo.wrongbook.vo.WrongBookVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
@Mapper
public interface WrongBookMapper_ZC extends BaseMapper<WrongBook> {
    @Select("SELECT wb.id AS wrongId, q.id AS questionId, q.content, q.type, " +
            "q.options_json AS optionsJson, q.answer, q.analysis, q.difficulty, " +
            "s.name AS subjectName, DATE_FORMAT(wb.create_time,'%Y-%m-%d') AS addTime " +
            "FROM wrong_book wb JOIN question q ON wb.question_id=q.id " +
            "JOIN subject s ON q.subject_id=s.id WHERE wb.user_id=#{userId} " +
            "ORDER BY wb.create_time DESC")
    Page<WrongBookVO> selectWrongPage(Page<WrongBookVO> page, @Param("userId") Long userId);
}