<template>
  <div class="dashboard-page">
    <div class="welcome-banner">
      <div class="banner-text">
        <h2>系统数据分析中心 📊</h2>
        <p>深入分析用户学习行为与题目质量。{{ isMock ? '（当前为分析模型演示数据）' : '（实时数据驱动）' }}</p>
      </div>
    </div>

    <div class="stat-grid">
      <div class="stat-card">
        <div class="stat-icon bg-blue"><el-icon><User /></el-icon></div>
        <div class="stat-info"><p>总注册用户</p><h3>{{ stats.totalUsers }}</h3></div>
      </div>
      <div class="stat-card">
        <div class="stat-icon bg-purple"><el-icon><Document /></el-icon></div>
        <div class="stat-info"><p>题库总数</p><h3>{{ stats.totalBanks }}</h3></div>
      </div>
      <div class="stat-card">
        <div class="stat-icon bg-orange"><el-icon><Collection /></el-icon></div>
        <div class="stat-info"><p>题目总数</p><h3>{{ stats.totalQuestions }}</h3></div>
      </div>
      <div class="stat-card">
        <div class="stat-icon bg-green"><el-icon><Trophy /></el-icon></div>
        <div class="stat-info"><p>累计答题次数</p><h3>{{ stats.totalAnswers }}</h3></div>
      </div>
    </div>

    <div class="analysis-layout">
      <div class="main-column">
        <div class="chart-card">
          <div class="card-header">近7日系统答题活跃度趋势</div>
          <div ref="trendChartRef" class="echarts-container trend-chart"></div>
        </div>

        <div class="chart-card">
          <div class="card-header">高频易错题预警 TOP 5 ⚠️</div>
          <div class="wrong-list">
            <div v-for="(item, index) in stats.wrongQuestionRank" :key="index" class="wrong-item">
              <div class="wrong-info">
                <span class="wrong-title">{{ index + 1 }}. {{ item.title }}</span>
                <span class="wrong-meta">作答 {{ item.attempts }} 次</span>
              </div>
              <div class="wrong-progress">
                <span class="rate-text">{{ item.errorRate }}% 错误率</span>
                <el-progress
                    :percentage="item.errorRate"
                    :color="customColors"
                    :show-text="false"
                    stroke-width="8"
                />
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="side-column">
        <div class="chart-card">
          <div class="card-header">题目画像分布</div>
          <div ref="diffChartRef" class="echarts-container half-chart"></div>
        </div>

        <div class="chart-card rank-card">
          <div class="card-header">活跃用户排行榜 🏆</div>
          <div class="rank-list">
            <div v-for="(item, index) in stats.topUsers" :key="index" class="rank-item">
              <div class="rank-left">
                <div class="rank-num" :class="'top-' + (index + 1)">{{ index + 1 }}</div>
                <div class="rank-avatar">{{ item.username.charAt(0).toUpperCase() }}</div>
                <span class="rank-name">{{ item.username }}</span>
              </div>
              <div class="rank-score">{{ item.score }} 题</div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive, nextTick, markRaw } from 'vue'
import { User, Document, Collection, Trophy } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import request from '../../utils/request.js'

const isMock = ref(false)

const stats = reactive({
  totalUsers: 0,
  totalQuestions: 0,
  totalBanks: 0,
  totalAnswers: 0,
  difficultyDistribution: [],
  topUsers: [],
  activityTrend: [],
  wrongQuestionRank: []
})

const trendChartRef = ref(null)
const diffChartRef = ref(null)
let trendChartInstance = null
let diffChartInstance = null

// 进度条颜色渐变：错误率越高颜色越红
const customColors = [
  { color: '#f56c6c', percentage: 100 },
  { color: '#e6a23c', percentage: 70 },
  { color: '#5cb87a', percentage: 40 }
]

// 具有深度业务属性的 Mock 数据
const MOCK_DATA = {
  totalUsers: 1284,
  totalQuestions: 3500,
  totalBanks: 24,
  totalAnswers: 15420,
  difficultyDistribution: [
    { name: '简单', value: 1800 },
    { name: '中等', value: 1200 },
    { name: '困难', value: 500 }
  ],
  topUsers: [
    { username: 'GALA_Lin', score: 1205 },
    { username: 'Jack_C', score: 980 },
    { username: 'AlexW', score: 875 },
    { username: 'Phony', score: 620 },
    { username: 'Student_99', score: 450 }
  ],
  // 深度分析：活跃度趋势
  activityTrend: [
    { date: '04-15', count: 120 },
    { date: '04-16', count: 210 },
    { date: '04-17', count: 180 },
    { date: '04-18', count: 350 },
    { date: '04-19', count: 290 },
    { date: '04-20', count: 420 },
    { date: '04-21', count: 510 }
  ],
  // 深度分析：贴合计算机网络的易错题
  wrongQuestionRank: [
    { title: 'TCP/IP参考模型中，哪一层负责端到端的可靠数据传输？', errorRate: 82, attempts: 450 },
    { title: '在子网掩码为255.255.255.192的网络中，最多可容纳的主机数是多少？', errorRate: 76, attempts: 382 },
    { title: 'Dijkstra算法在计算网络最短路径时，时间复杂度是多少？', errorRate: 68, attempts: 315 },
    { title: '关于UDP协议，以下说法中错误的是？', errorRate: 54, attempts: 520 },
    { title: '数据挖掘中，Apriori算法的核心原理是？', errorRate: 45, attempts: 260 }
  ]
}

const fetchData = async () => {
  try {
    const res = await request.get('/api/admin/dashboard/stats')
    if (res.code === 200) {
      const data = res.data
      // 只要趋势或错题为空，我们为了展示效果就启用深度 Mock
      if (!data.activityTrend || data.activityTrend.length === 0 || data.totalQuestions === 0) {
        isMock.value = true
        Object.assign(stats, MOCK_DATA)
      } else {
        isMock.value = false
        Object.assign(stats, data)
      }
      await nextTick()
      initCharts()
    }
  } catch (error) {
    isMock.value = true
    Object.assign(stats, MOCK_DATA)
    await nextTick()
    initCharts()
  }
}

const initCharts = () => {
  // 1. 活跃度趋势面积折线图
  if (trendChartRef.value) {
    trendChartInstance = markRaw(echarts.init(trendChartRef.value))
    const dates = stats.activityTrend.map(item => item.date)
    const counts = stats.activityTrend.map(item => item.count)

    trendChartInstance.setOption({
      tooltip: { trigger: 'axis', backgroundColor: 'rgba(255,255,255,0.9)', borderColor: '#eee' },
      grid: { left: '3%', right: '4%', bottom: '3%', top: '10%', containLabel: true },
      xAxis: { type: 'category', boundaryGap: false, data: dates, axisLine: { lineStyle: { color: '#e8eaf0' } }, axisLabel: { color: '#6b7280' } },
      yAxis: { type: 'value', splitLine: { lineStyle: { type: 'dashed', color: '#f3f4f8' } }, axisLabel: { color: '#6b7280' } },
      series: [
        {
          name: '答题次数',
          type: 'line',
          smooth: true,
          symbolSize: 8,
          itemStyle: { color: '#4f6ef7' },
          areaStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: 'rgba(79, 110, 247, 0.4)' },
              { offset: 1, color: 'rgba(79, 110, 247, 0.0)' }
            ])
          },
          data: counts
        }
      ]
    })
  }

  // 2. 难度分布环形图
  if (diffChartRef.value) {
    diffChartInstance = markRaw(echarts.init(diffChartRef.value))
    diffChartInstance.setOption({
      tooltip: { trigger: 'item' },
      legend: { bottom: '0', left: 'center', itemWidth: 10, itemHeight: 10 },
      color: ['#12b76a', '#f79009', '#f5533d'],
      series: [
        {
          name: '题目难度',
          type: 'pie',
          radius: ['45%', '70%'],
          center: ['50%', '45%'],
          avoidLabelOverlap: false,
          itemStyle: { borderRadius: 5, borderColor: '#fff', borderWidth: 2 },
          label: { show: false },
          data: stats.difficultyDistribution
        }
      ]
    })
  }
}

window.addEventListener('resize', () => {
  trendChartInstance?.resize()
  diffChartInstance?.resize()
})

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.dashboard-page { padding: 30px; background: #f3f4f8; min-height: calc(100vh - 56px); }
.welcome-banner { background: linear-gradient(135deg, #2b324d 0%, #1a1d2e 100%); border-radius: 16px; padding: 24px 32px; color: #fff; margin-bottom: 24px; box-shadow: 0 8px 20px rgba(0,0,0,0.1); }
.welcome-banner h2 { margin: 0 0 8px 0; font-size: 22px; }
.welcome-banner p { margin: 0; opacity: 0.7; font-size: 14px; }

/* 顶栏卡片 */
.stat-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(220px, 1fr)); gap: 20px; margin-bottom: 24px; }
.stat-card { background: #fff; padding: 20px; border-radius: 16px; display: flex; align-items: center; gap: 16px; box-shadow: 0 2px 10px rgba(0,0,0,0.02); }
.stat-icon { width: 48px; height: 48px; border-radius: 12px; display: flex; align-items: center; justify-content: center; font-size: 20px; color: #fff; }
.bg-blue { background: linear-gradient(135deg, #4f6ef7, #7c91fa); }
.bg-purple { background: linear-gradient(135deg, #8b5cf6, #a78bfa); }
.bg-orange { background: linear-gradient(135deg, #f79009, #fdb022); }
.bg-green { background: linear-gradient(135deg, #12b76a, #32d583); }
.stat-info p { margin: 0 0 4px 0; color: #6b7280; font-size: 13px; }
.stat-info h3 { margin: 0; color: #1a1d2e; font-size: 24px; font-weight: 700; }

/* 深度分析布局 */
.analysis-layout { display: grid; grid-template-columns: 2fr 1fr; gap: 20px; }
.main-column { display: flex; flex-direction: column; gap: 20px; }
.side-column { display: flex; flex-direction: column; gap: 20px; }

.chart-card { background: #fff; border-radius: 16px; padding: 24px; box-shadow: 0 2px 10px rgba(0,0,0,0.02); flex: 1; }
.card-header { font-size: 16px; font-weight: 700; color: #1a1d2e; margin-bottom: 20px; }

/* 图表尺寸 */
.trend-chart { height: 260px; width: 100%; }
.half-chart { height: 220px; width: 100%; }

/* 错题列表 */
.wrong-list { display: flex; flex-direction: column; gap: 16px; }
.wrong-item { display: flex; flex-direction: column; gap: 8px; padding-bottom: 12px; border-bottom: 1px dashed #f3f4f8; }
.wrong-item:last-child { border-bottom: none; padding-bottom: 0; }
.wrong-info { display: flex; justify-content: space-between; align-items: flex-start; }
.wrong-title { font-size: 14px; font-weight: 600; color: #374151; line-height: 1.5; flex: 1; padding-right: 16px; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
.wrong-meta { font-size: 12px; color: #9ca3af; white-space: nowrap; }
.wrong-progress { display: flex; align-items: center; gap: 12px; }
.rate-text { font-size: 13px; color: #f56c6c; font-weight: 600; min-width: 70px; }
:deep(.el-progress) { flex: 1; }

/* 排行榜 */
.rank-list { display: flex; flex-direction: column; gap: 12px; }
.rank-item { display: flex; align-items: center; justify-content: space-between; padding: 8px 0; }
.rank-left { display: flex; align-items: center; gap: 12px; }
.rank-num { width: 22px; height: 22px; border-radius: 50%; background: #f3f4f8; color: #6b7280; display: flex; align-items: center; justify-content: center; font-size: 12px; font-weight: 700; }
.rank-num.top-1 { background: #fee4e2; color: #f04438; }
.rank-num.top-2 { background: #fef0c7; color: #f79009; }
.rank-num.top-3 { background: #d1fadf; color: #12b76a; }
.rank-avatar { width: 30px; height: 30px; border-radius: 8px; background: #eef1fe; color: #4f6ef7; display: flex; align-items: center; justify-content: center; font-weight: 700; font-size: 14px; }
.rank-name { font-weight: 600; color: #374151; font-size: 13px; }
.rank-score { font-weight: 700; color: #4f6ef7; font-size: 13px; }

@media (max-width: 1024px) {
  .analysis-layout { grid-template-columns: 1fr; }
}
</style>