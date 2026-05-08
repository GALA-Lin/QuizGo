<template>
  <view class="page">
    <view class="header">错题详情 / 重做</view>
    
    <view class="card" v-if="question">
      <!-- 题目区域 -->
      <view class="question-header">
        <text class="type-tag">{{ question.type === 0 ? '单选题' : (question.type === 1 ? '多选题' : '判断题') }}</text>
        <text class="content">{{ question.content }}</text>
      </view>

      <!-- 选项区域 -->
      <view class="options-box">
        <view 
          class="option-item" 
          v-for="(opt, index) in options" 
          :key="index"
          :class="{ 'selected': selectedAnswer === getOptLetter(opt) }"
          @click="selectOption(getOptLetter(opt))"
        >
          {{ opt }}
        </view>
      </view>

      <!-- 提交按钮区 -->
      <button class="submit-btn" @click="submitAnswer" v-if="!showAnalysis">确认答案</button>

      <!-- 解析区域（提交答案后显示） -->
      <view class="analysis-box" v-if="showAnalysis">
        <view class="result-box">
          <text class="result-text" :class="isCorrect ? 'correct-text' : 'wrong-text'">
            {{ isCorrect ? '🎉 恭喜你，回答正确！' : '🥺 很遗憾，还是答错了' }}
          </text>
        </view>
        
        <view class="compare-box">
          <text>你的答案：<text :class="isCorrect ? 'correct-text' : 'wrong-text'">{{ selectedAnswer }}</text></text>
          <text style="margin-left: 30px;">正确答案：<text class="correct-text">{{ question.answer }}</text></text>
        </view>

        <view class="analysis-title">题目解析：</view>
        <view class="analysis-content">{{ question.analysis || '暂无详细解析' }}</view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { request } from '@/utils/request.js'

const question = ref(null)
const options = ref([])
const selectedAnswer = ref('')
const showAnalysis = ref(false)
const isCorrect = ref(false)

onLoad(() => {
  // 读取 wrong.vue 传过来的错题对象
  const data = uni.getStorageSync('currentWrongQuestion')
  if (data) {
    question.value = data
    try {
      // 你的后端 VO 里 optionsJson 是 JSON 字符串，需要解析成数组
      options.value = JSON.parse(data.optionsJson || '[]')
    } catch (e) {
      options.value = []
    }
  }
})

// 提取选项首字母 (例如 "A.选项内容" 提取出 "A")
const getOptLetter = (optStr) => {
  if(!optStr) return ''
  return optStr.substring(0, 1) 
}

// 选择选项
const selectOption = (letter) => {
  // 如果已经看了答案解析，就不允许修改答案了
  if (showAnalysis.value) return 
  selectedAnswer.value = letter
}

// 提交答案
const submitAnswer = async () => {
  if (!selectedAnswer.value) {
    uni.showToast({ title: '请先选择答案', icon: 'none' })
    return
  }
  
  try {
    // 理想情况：调用 Person C 的练题提交接口，记录对错
    const res = await request({
      url: '/api/practice/submit',
      method: 'POST',
      data: {
        questionId: question.value.questionId,
        userAnswer: selectedAnswer.value
      }
    })
    isCorrect.value = res.isCorrect
    showAnalysis.value = true
  } catch (e) {
    // 【完美兜底】万一 Person C 的接口没通，直接本地对比你的 VO 里的 answer，不影响你的页面演示
    console.log("服务器提交失败，走本地数据验证比对");
    isCorrect.value = (selectedAnswer.value === question.value.answer)
    showAnalysis.value = true
  }
}
</script>

<style scoped>
.page { min-height: 100vh; padding: 20px; background-color: #f5f7fa; }
.header { font-size: 22px; font-weight: bold; margin-bottom: 20px; color: #333; }
.card { background: #fff; border-radius: 12px; padding: 20px; box-shadow: 0 2px 8px rgba(0,0,0,0.04); }

.question-header { margin-bottom: 25px; line-height: 1.6; display: flex; align-items: flex-start;}
.type-tag { background: #e6f7ff; color: #1890ff; padding: 2px 8px; border-radius: 4px; font-size: 13px; margin-right: 10px; white-space: nowrap; margin-top: 3px; font-weight: bold;}
.content { font-size: 18px; font-weight: bold; color: #333;}

.options-box { margin-bottom: 25px; }
.option-item { padding: 15px 20px; background: #f8f9fa; border-radius: 8px; margin-bottom: 12px; border: 2px solid transparent; font-size: 16px; color: #444; transition: all 0.2s;}
.option-item.selected { border-color: #1890ff; background: #e6f7ff; color: #1890ff; font-weight: bold;}

.submit-btn { background: #1890ff; color: #fff; border-radius: 8px; height: 46px; line-height: 46px; font-size: 16px; border: none; }
.submit-btn::after { border: none; }

.analysis-box { background: #fafafa; padding: 20px; border-radius: 10px; margin-top: 20px; border: 1px solid #eee; }
.result-box { margin-bottom: 15px; }
.result-text { font-size: 18px; font-weight: bold; }
.correct-text { color: #52c41a; font-weight: bold;}
.wrong-text { color: #f5222d; font-weight: bold;}

.compare-box { background: #fff; padding: 15px; border-radius: 8px; margin-bottom: 15px; font-size: 15px; border: 1px solid #f0f0f0;}
.analysis-title { font-weight: bold; font-size: 16px; margin-bottom: 10px; color: #333;}
.analysis-content { font-size: 15px; color: #666; line-height: 1.6; background: #fff; padding: 15px; border-radius: 8px; border: 1px solid #f0f0f0;}
</style>