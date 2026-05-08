package com.cupk.quizgo.question.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.util.ListUtils;
import com.cupk.quizgo.question.dto.QuestionExcelDTO;
import com.cupk.quizgo.question.entity.Question;
import com.cupk.quizgo.question.service.IQuestionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * 核心引擎：逐行读取 Excel 并批量入库
 * 注意：Listener 不能被 Spring 托管（不能加 @Component），每次读取都要 new 一个新的
 */
@Slf4j
public class QuestionExcelListener implements ReadListener<QuestionExcelDTO> {

    // 每隔100条存数据库，实际使用中可以3000条，然后清理list，方便内存回收
    private static final int BATCH_COUNT = 100;
    private List<Question> cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);

    // 把 Service 传进来，用于操作数据库
    private final IQuestionService questionService;
    private final Long bankId;
    private final Long subjectId;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // 构造函数
    public QuestionExcelListener(IQuestionService questionService, Long bankId, Long subjectId) {
        this.questionService = questionService;
        this.bankId = bankId;
        this.subjectId = subjectId;
    }

    /**
     * 这个方法每一条数据解析都会来调用
     */
    @Override
    public void invoke(QuestionExcelDTO dto, AnalysisContext context) {
        log.info("解析到一条数据: {}", dto.getContent());

        // 1. 将 DTO 转换为数据库实体 Question
        Question question = new Question();
        question.setBankId(this.bankId);
        question.setSubjectId(this.subjectId);
        question.setContent(dto.getContent());
        question.setAnswer(dto.getStandardAnswer());
        question.setAnalysis(dto.getAnalysis());
        question.setDifficulty(1); // 默认难度

        // 2. 转换题型 (字符串转数字)
        int type = 0;
        if ("多选".equals(dto.getType())) {
            type = 1;
        } else if ("判断".equals(dto.getType())) {
            type = 2;
        }
        question.setType(type);

        // 3. 拼接选项 JSON
        try {
            if (type == 2) {
                // 判断题固定选项
                question.setOptionsJson("[\"正确\",\"错误\"]");
            } else {
                // 单选/多选：将非空的选项拼接成 JSON 数组
                List<String> options = new ArrayList<>();
                if (dto.getOptionA() != null && !dto.getOptionA().isBlank()) options.add("A." + dto.getOptionA());
                if (dto.getOptionB() != null && !dto.getOptionB().isBlank()) options.add("B." + dto.getOptionB());
                if (dto.getOptionC() != null && !dto.getOptionC().isBlank()) options.add("C." + dto.getOptionC());
                if (dto.getOptionD() != null && !dto.getOptionD().isBlank()) options.add("D." + dto.getOptionD());
                question.setOptionsJson(objectMapper.writeValueAsString(options));
            }
        } catch (JsonProcessingException e) {
            log.error("JSON转换失败", e);
            question.setOptionsJson("[]");
        }

        // 4. 加入缓存列表
        cachedDataList.add(question);

        // 5. 达到 BATCH_COUNT 了，去存一次数据库，防止内存溢出
        if (cachedDataList.size() >= BATCH_COUNT) {
            saveData();
            // 存储完成清理 list
            cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
        }
    }

    /**
     * 所有数据解析完成了 都会来调用
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        // 保存最后剩下的一批数据
        if (!cachedDataList.isEmpty()) {
            saveData();
        }
        log.info("所有数据解析完成！");
    }

    /**
     * 批量存入数据库
     */
    private void saveData() {
        log.info("{} 条数据，开始存储数据库！", cachedDataList.size());
        questionService.saveBatch(cachedDataList);
        log.info("存储数据库成功！");
    }
}