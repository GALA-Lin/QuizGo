<template>
  <view class="container">
    <!-- 顶部欢迎语 -->
    <view class="greeting-area">
      <text class="greeting">👋 你好，{{ nickname }}</text>
      <text class="greeting-sub">选一个科目开始刷题吧</text>
    </view>

    <!-- 加载中 -->
    <view v-if="loading" class="center-tip">
      <text>加载中...</text>
    </view>

    <!-- 科目为空 -->
    <view v-else-if="subjectList.length === 0" class="center-tip">
      <text>暂无科目，请联系管理员添加</text>
    </view>

    <!-- 科目列表 -->
    <view v-else>
      <view
        class="card subject-card"
        v-for="item in subjectList"
        :key="item.id"
        @click="goQuiz(item)"
      >
        <view class="subject-icon">{{ iconMap[item.id % iconMap.length] }}</view>
        <view class="subject-info">
          <text class="subject-name">{{ item.name }}</text>
          <text class="subject-desc">{{ item.description || '点击开始刷题' }}</text>
        </view>
        <text class="arrow">›</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { request } from '@/utils/request.js'

const subjectList = ref([])
const loading = ref(false)
const nickname = ref('同学')

// 科目图标轮换（用 id 取余，不管多少科目都有图标）
const iconMap = ['📚', '🔢', '🧪', '🌏', '🖥️', '🎨', '📖', '🔬']

onShow(() => {
  // 读取本地登录信息显示昵称
  const info = uni.getStorageSync('userInfo')
  if (info && info.username) nickname.value = info.username

  fetchSubjects()
})

const fetchSubjects = async () => {
  loading.value = true
  try {
    const res = await request({ url: '/api/subject/list', method: 'GET' })
    // 后端可能直接返回数组，也可能在 records 里
    if (Array.isArray(res)) {
      subjectList.value = res
    } else if (res && res.records) {
      subjectList.value = res.records
    }
  } catch (e) {
    uni.showToast({ title: '获取科目失败', icon: 'none' })
  } finally {
    loading.value = false
  }
}

const goQuiz = (item) => {
  uni.navigateTo({ url: `/pages/quiz/quiz?subjectId=${item.id}&subjectName=${item.name}` })
}
</script>

<style scoped>
.container { padding: 20px; background: #f5f7fa; min-height: 100vh; }

.greeting-area { margin-bottom: 24px; padding: 10px 0; }
.greeting { font-size: 22px; font-weight: bold; color: #333; display: block; }
.greeting-sub { font-size: 14px; color: #999; margin-top: 6px; display: block; }

.center-tip { text-align: center; margin-top: 80px; color: #bbb; font-size: 15px; }

.subject-card {
  display: flex;
  align-items: center;
  padding: 18px 20px;
  margin-bottom: 14px;
  border-radius: 14px;
  cursor: pointer;
}
.subject-card:active { transform: scale(0.98); }

.subject-icon { font-size: 36px; margin-right: 16px; }
.subject-info { flex: 1; }
.subject-name { font-size: 18px; font-weight: bold; color: #333; display: block; }
.subject-desc { font-size: 13px; color: #999; margin-top: 4px; display: block; }
.arrow { font-size: 24px; color: #ccc; font-weight: bold; }
</style>