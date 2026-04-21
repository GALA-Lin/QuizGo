package com.cupk.quizgo.question.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cupk.quizgo.common.result.Result;
import com.cupk.quizgo.entity.User;
import com.cupk.quizgo.question.entity.Question;
import com.cupk.quizgo.question.mapper.QuestionBankMapper;
import com.cupk.quizgo.question.mapper.QuestionMapper_ZZ;
import com.cupk.quizgo.user.mapper.UserMapper;
import com.cupk.quizgo.question.vo.DashboardVO;
import com.cupk.quizgo.module.learning.mapper.AnswerRecordMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final UserMapper userMapper;
    private final QuestionMapper_ZZ questionMapper;
    private final QuestionBankMapper questionBankMapper;
    private final AnswerRecordMapper answerRecordMapper;

    @GetMapping("/stats")
    public Result<DashboardVO> getStats() {
        Long totalUsers = userMapper.selectCount(null);
        Long totalQuestions = questionMapper.selectCount(null);

        // 核心判断：如果数据库里一题都没有（或用户极少），直接返回精美的演示静态数据
        if (totalQuestions == 0) {
            return Result.success(getMockDashboardData());
        }

        // =======================
        // 以下为真实数据库数据聚合逻辑
        // =======================
        DashboardVO vo = new DashboardVO();
        vo.setTotalUsers(totalUsers);
        vo.setTotalQuestions(totalQuestions);
        vo.setTotalBanks(questionBankMapper.selectCount(null));

        try {
            vo.setTotalAnswers(answerRecordMapper.selectCount(null));
        } catch (Exception e) {
            vo.setTotalAnswers(0L); // 容错处理
        }

        // 获取所有题目用于内存聚合分析
        List<Question> questions = questionMapper.selectList(null);

        // 1. 难度分布统计 (1简单 2中等 3困难)
        long easy = questions.stream().filter(q -> q.getDifficulty() != null && q.getDifficulty() == 1).count();
        long medium = questions.stream().filter(q -> q.getDifficulty() != null && q.getDifficulty() == 2).count();
        long hard = questions.stream().filter(q -> q.getDifficulty() != null && q.getDifficulty() == 3).count();

        List<Map<String, Object>> diffList = new ArrayList<>();
        diffList.add(Map.of("name", "简单", "value", easy));
        diffList.add(Map.of("name", "中等", "value", medium));
        diffList.add(Map.of("name", "困难", "value", hard));
        vo.setDifficultyDistribution(diffList);

        // 2. 题型分布统计 (0单选 1多选 2判断)
        long singleChoice = questions.stream().filter(q -> q.getType() != null && q.getType() == 0).count();
        long multiChoice = questions.stream().filter(q -> q.getType() != null && q.getType() == 1).count();
        long judge = questions.stream().filter(q -> q.getType() != null && q.getType() == 2).count();

        List<Map<String, Object>> typeList = new ArrayList<>();
        typeList.add(Map.of("name", "单选题", "value", singleChoice));
        typeList.add(Map.of("name", "多选题", "value", multiChoice));
        typeList.add(Map.of("name", "判断题", "value", judge));
        vo.setTypeDistribution(typeList);

        // 3. 用户活跃度排行榜 (取最新注册的5个用户)
        QueryWrapper<User> userQw = new QueryWrapper<>();
        userQw.orderByDesc("id").last("LIMIT 5");
        List<User> users = userMapper.selectList(userQw);
        List<Map<String, Object>> topUsers = new ArrayList<>();
        for (int i = 0; i < users.size(); i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("username", users.get(i).getUsername());
            map.put("score", (5 - i) * 20 + 10); // 暂用公式模拟得分
            topUsers.add(map);
        }
        vo.setTopUsers(topUsers);

        // 4. 近7日活跃度趋势与易错题 (如果是真实数据，需要复杂SQL统计。这里防止异常，提供空列表容错)
        // 注意：如果你后续在 AnswerRecord 里记录了详细的答题日志，可以在这里用 Stream 按日期和题号做 GroupBy 聚合
        vo.setActivityTrend(new ArrayList<>());
        vo.setWrongQuestionRank(new ArrayList<>());

        return Result.success(vo);
    }

    /**
     * 生成静态演示数据 (当数据库为空时调用，保证面板展示不单调)
     */
    private DashboardVO getMockDashboardData() {
        DashboardVO mock = new DashboardVO();
        mock.setTotalUsers(1284L);
        mock.setTotalQuestions(3500L);
        mock.setTotalBanks(24L);
        mock.setTotalAnswers(15420L);

        // 画像数据
        mock.setDifficultyDistribution(List.of(
                Map.of("name", "简单", "value", 1800),
                Map.of("name", "中等", "value", 1200),
                Map.of("name", "困难", "value", 500)
        ));
        mock.setTypeDistribution(List.of(
                Map.of("name", "单选题", "value", 2000),
                Map.of("name", "多选题", "value", 800),
                Map.of("name", "判断题", "value", 700)
        ));

        // 用户榜单
        mock.setTopUsers(List.of(
                Map.of("username", "GALA_Lin", "score", 1205),
                Map.of("username", "Jack_C", "score", 980),
                Map.of("username", "AlexW", "score", 875),
                Map.of("username", "Phony", "score", 620),
                Map.of("username", "Student_99", "score", 450)
        ));

        // 近7天动态生成日期趋势
        List<Map<String, Object>> trends = new ArrayList<>();
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");
        int[] dummyCounts = {120, 210, 180, 350, 290, 420, 510};
        for (int i = 6; i >= 0; i--) {
            trends.add(Map.of(
                    "date", today.minusDays(i).format(formatter),
                    "count", dummyCounts[6 - i]
            ));
        }
        mock.setActivityTrend(trends);

        // 易错题雷区
        mock.setWrongQuestionRank(List.of(
                Map.of("title", "TCP/IP参考模型中，哪一层负责端到端的可靠数据传输？", "errorRate", 82, "attempts", 450),
                Map.of("title", "在子网掩码为255.255.255.192的网络中，最多可容纳的主机数是多少？", "errorRate", 76, "attempts", 382),
                Map.of("title", "Dijkstra算法在计算网络最短路径时，时间复杂度是多少？", "errorRate", 68, "attempts", 315),
                Map.of("title", "关于UDP协议，以下说法中错误的是？", "errorRate", 54, "attempts", 520),
                Map.of("title", "数据挖掘中，Apriori算法的核心原理是？", "errorRate", 45, "attempts", 260)
        ));

        return mock;
    }
}