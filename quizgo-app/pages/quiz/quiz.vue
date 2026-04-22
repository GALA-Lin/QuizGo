<template>
  <view class="page">

    <!-- 题目加载中 -->
    <view v-if="loadingQuestions" class="center-tip">
      <text>题目加载中...</text>
    </view>

    <!-- 无题目 -->
    <view v-else-if="questions.length === 0" class="center-tip">
      <text>该科目暂无题目 😅</text>
    </view>

    <!-- 刷题主体 -->
    <view v-else>
      <!-- 进度条 -->
      <view class="progress-bar-wrap">
        <view class="progress-bar" :style="{ width: progressPct + '%' }"></view>
      </view>
      <view class="progress-text">
        <text>{{ currentIndex + 1 }} / {{ questions.length }}</text>
        <text>{{ subjectName }}</text>
      </view>

      <!-- 题目卡片 -->
      <view class="card question-card">
        <view class="type-row">
          <text class="type-badge">{{ typeLabel }}</text>
          <text class="difficulty">{{ diffLabel }}</text>
        </view>
        <text class="question-content">{{ current.content }}</text>

        <!-- 选项 -->
        <view class="options-box">
          <view
            class="option-item"
            v-for="(opt, idx) in currentOptions"
            :key="idx"
            :class="optionClass(opt)"
            @click="selectOption(opt)"
          >
            {{ opt }}
          </view>
        </view>
      </view>

      <!-- 提交按钮 -->
      <button
        class="submit-btn"
        v-if="!showResult"
        :disabled="!selectedAnswer"
        @click="submitAnswer"
      >
        确认答案
      </button>

      <!-- 答题结果与解析 -->
      <view class="card result-card" v-if="showResult">
        <view class="result-row">
          <text class="result-icon">{{ isCorrect ? '🎉' : '🥺' }}</text>
          <text :class="['result-label', isCorrect ? 'correct-text' : 'wrong-text']">
            {{ isCorrect ? '回答正确！' : '回答错误' }}
          </text>
        </view>
        <view class="answer-compare" v-if="!isCorrect">
          <text>你的答案：<text class="wrong-text">{{ selectedAnswer }}</text></text>
          <text style="margin-left: 20px;">正确答案：<text class="correct-text">{{ correctAnswer }}</text></text>
        </view>
        <view v-if="analysis" class="analysis-box">
          <text class="analysis-title">💡 解析</text>
          <text class="analysis-content">{{ analysis }}</text>
        </view>

        <!-- 下一题 / 完成 -->
        <button class="next-btn" @click="nextQuestion">
          {{ currentIndex < questions.length - 1 ? '下一题 →' : '完成刷题 🏁' }}
        </button>
      </view>
    </view>

  </view>
</template>

<script setup>
import { ref, computed } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { request } from '@/utils/request.js'

// 路由参数
const subjectId = ref('')
const subjectName = ref('')

// 题目数据
const questions = ref([])
const loadingQuestions = ref(true)
const currentIndex = ref(0)

// 答题状态
const selectedAnswer = ref('')   // 当前选中的选项首字母，如 "A"
const showResult = ref(false)
const isCorrect = ref(false)
const correctAnswer = ref('')
const analysis = ref('')

// ---- 计算属性 ----
const current = computed(() => questions.value[currentIndex.value] || {})

const currentOptions = computed(() => {
  try {
    return JSON.parse(current.value.optionsJson || '[]')
  } catch {
    return []
  }
})

const progressPct = computed(() =>
  questions.value.length ? Math.round(((currentIndex.value + 1) / questions.value.length) * 100) : 0
)

const typeLabel = computed(() => {
  const map = { 0: '单选题', 1: '多选题', 2: '判断题' }
  return map[current.value.type] ?? '单选题'
})

const diffLabel = computed(() => {
  const map = { 1: '⭐ 简单', 2: '⭐⭐ 中等', 3: '⭐⭐⭐ 较难' }
  return map[current.value.difficulty] ?? ''
})

// 选项样式：未提交时高亮选中，提交后显示对错
const optionClass = (opt) => {
  const letter = opt.substring(0, 1)
  if (!showResult.value) {
    return selectedAnswer.value === letter ? 'selected' : ''
  }
  if (letter === correctAnswer.value) return 'opt-correct'
  if (letter === selectedAnswer.value && !isCorrect.value) return 'opt-wrong'
  return ''
}

// ---- 生命周期 ----
onLoad((query) => {
  subjectId.value = query.subjectId || '1'
  subjectName.value = decodeURIComponent(query.subjectName || '刷题')
  fetchQuestions()
})

// ---- 方法 ----
const fetchQuestions = async () => {
  loadingQuestions.value = true
  try {
    const res = await request({
      url: '/api/question/list',
      method: 'GET',
      data: { subjectId: subjectId.value, page: 1, size: 20 }
    })
    questions.value = res.records || []
  } catch (e) {
    uni.showToast({ title: '获取题目失败', icon: 'none' })
  } finally {
    loadingQuestions.value = false
  }
}

const selectOption = (opt) => {
  if (showResult.value) return
  selectedAnswer.value = opt.substring(0, 1)
}

const submitAnswer = async () => {
  if (!selectedAnswer.value) return
  try {
    const res = await request({
      url: '/api/practice/submit',
      method: 'POST',
      data: {
        questionId: current.value.id,
        userAnswer: selectedAnswer.value
      }
    })
    // res = { isCorrect, correctAnswer, analysis, addedToWrongBook }
    isCorrect.value = res.isCorrect
    correctAnswer.value = res.correctAnswer || selectedAnswer.value
    analysis.value = res.analysis || ''
  } catch (e) {
    // 接口异常时，前端无法知道正确答案，仅展示已选
    isCorrect.value = false
    correctAnswer.value = '?'
    analysis.value = '网络异常，无法获取解析'
  }
  showResult.value = true
}

const nextQuestion = () => {
  if (currentIndex.value < questions.value.length - 1) {
    currentIndex.value++
    selectedAnswer.value = ''
    showResult.value = false
    isCorrect.value = false
    correctAnswer.value = ''
    analysis.value = ''
  } else {
    // 全部答完，返回首页
    uni.showToast({ title: '本轮刷题完成！', icon: 'success' })
    setTimeout(() => uni.switchTab({ url: '/pages/index/index' }), 1200)
  }
}
</script>

<style scoped>
.page { min-height: 100vh; background: #f5f7fa; padding: 16px 16px 40px; }

.center-tip { text-align: center; margin-top: 120px; font-size: 15px; color: #bbb; }

/* 进度条 */
.progress-bar-wrap {
  height: 6px;
  background: #e0e0e0;
  border-radius: 3px;
  margin-bottom: 8px;
  overflow: hidden;
}
.progress-bar {
  height: 100%;
  background: linear-gradient(90deg, #2979ff, #2196f3);
  border-radius: 3px;
  transition: width 0.3s;
}
.progress-text {
  display: flex;
  justify-content: space-between;
  font-size: 13px;
  color: #999;
  margin-bottom: 16px;
}

/* 题目卡片 */
.question-card { padding: 22px; border-radius: 14px; margin-bottom: 16px; }
.type-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}
.type-badge {
  background: #e6f0ff;
  color: #2979ff;
  padding: 3px 10px;
  border-radius: 6px;
  font-size: 13px;
  font-weight: bold;
}
.difficulty { font-size: 13px; color: #f9a825; }
.question-content { font-size: 18px; color: #222; line-height: 1.7; font-weight: 500; display: block; }

/* 选项 */
.options-box { margin-top: 20px; }
.option-item {
  padding: 14px 18px;
  background: #f8f9fb;
  border-radius: 10px;
  margin-bottom: 12px;
  font-size: 16px;
  color: #444;
  border: 2px solid transparent;
  transition: all 0.15s;
}
.option-item:active { opacity: 0.8; }
.option-item.selected { border-color: #2979ff; background: #e6f0ff; color: #2979ff; font-weight: bold; }
.option-item.opt-correct { border-color: #4caf50; background: #e8f5e9; color: #2e7d32; font-weight: bold; }
.option-item.opt-wrong { border-color: #f44336; background: #ffebee; color: #c62828; font-weight: bold; }

/* 提交按钮 */
.submit-btn {
  width: 100%;
  height: 50px;
  line-height: 50px;
  background: #2979ff;
  color: #fff;
  border-radius: 12px;
  font-size: 17px;
  font-weight: bold;
  border: none;
  letter-spacing: 2px;
}
.submit-btn::after { border: none; }
.submit-btn[disabled] { background: #b0c4ff; }

/* 结果卡片 */
.result-card { padding: 22px; border-radius: 14px; }
.result-row { display: flex; align-items: center; margin-bottom: 12px; }
.result-icon { font-size: 28px; margin-right: 10px; }
.result-label { font-size: 20px; font-weight: bold; }
.correct-text { color: #4caf50; }
.wrong-text { color: #f44336; }

.answer-compare {
  background: #fafafa;
  padding: 12px 16px;
  border-radius: 8px;
  font-size: 15px;
  margin-bottom: 14px;
  border: 1px solid #f0f0f0;
}
.analysis-box {
  background: #fffde7;
  padding: 14px 16px;
  border-radius: 8px;
  margin-bottom: 18px;
  border-left: 4px solid #ffd600;
}
.analysis-title { font-size: 14px; font-weight: bold; color: #f9a825; display: block; margin-bottom: 6px; }
.analysis-content { font-size: 15px; color: #555; line-height: 1.7; display: block; }

.next-btn {
  width: 100%;
  height: 48px;
  line-height: 48px;
  background: #2979ff;
  color: #fff;
  border-radius: 10px;
  font-size: 16px;
  font-weight: bold;
  border: none;
}
.next-btn::after { border: none; }
</style>