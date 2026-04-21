package com.cupk.quizgo.question.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * 用于读取 Excel 的 DTO 映射类
 * 注意：@ExcelProperty 里的文字，必须和将来提供给管理员下载的 Excel 模板第一行的表头【一字不差】！
 */
@Data
public class QuestionExcelDTO {

    @ExcelProperty("题目内容")
    private String content;

    @ExcelProperty("题型(单选/多选/判断)")
    private String type;

    @ExcelProperty("选项A")
    private String optionA;

    @ExcelProperty("选项B")
    private String optionB;

    @ExcelProperty("选项C")
    private String optionC;

    @ExcelProperty("选项D")
    private String optionD;

    @ExcelProperty("正确答案")
    private String standardAnswer;

    @ExcelProperty("答案解析")
    private String analysis;
}