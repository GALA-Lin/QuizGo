package com.cupk.quizgo.question.vo;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class DashboardVO {
    private Long totalUsers;
    private Long totalQuestions;
    private Long totalBanks;
    private Long totalAnswers;

    private List<Map<String, Object>> difficultyDistribution;
    private List<Map<String, Object>> typeDistribution;
    private List<Map<String, Object>> topUsers;

    // --- 新增深度分析字段 ---
    // 近7日答题活跃度趋势 (包含日期 date 和 数量 count)
    private List<Map<String, Object>> activityTrend;

    // 高频易错题排行榜 (包含 题目内容 title, 错误率 errorRate, 尝试次数 attempts)
    private List<Map<String, Object>> wrongQuestionRank;
}