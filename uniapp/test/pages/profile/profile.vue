<template>
  <view class="container">
    <!-- 个人中心（D 负责） -->
    
    <!-- 1. 用户信息头部区域 -->
    <view class="card header-card">
      <image class="avatar" src="/static/default-avatar.png" mode="aspectFill" />
      <view class="user-info">
        <text class="username">{{ userInfo.username || '未登录' }}</text>
        <view class="tags">
          <!-- 角色标识：0是普通用户，1是管理员 -->
          <text class="role-tag admin-tag" v-if="userInfo.role === 1">管理员</text>
          <text class="role-tag user-tag" v-else-if="userInfo.username">普通用户</text>
        </view>
      </view>
    </view>

    <!-- 2. 答题数据统计区域 -->
    <view class="card stats-card">
      <view class="title" style="margin-bottom: 15px; font-size: 16px;">我的答题数据</view>
      <view class="stats-grid">
        <view class="stat-item">
          <text class="num">{{ userInfo.totalAnswered || 0 }}</text>
          <text class="label">累计答题</text>
        </view>
        <view class="stat-item">
          <text class="num correct">{{ userInfo.correctCount || 0 }}</text>
          <text class="label">答对题数</text>
        </view>
        <view class="stat-item">
          <text class="num wrong">{{ userInfo.wrongCount || 0 }}</text>
          <text class="label">当前错题</text>
        </view>
      </view>
    </view>

    <!-- 3. 操作按钮区域 -->
    <view class="action-area">
      <button class="logout-btn" @click="handleLogout">退出登录</button>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
// 假设你们封装好的 request 在 utils 目录下
import { request } from '@/utils/request.js'

// 存储用户信息数据
const userInfo = ref({})

// 每次切换到“我的”这个 Tab 页时，都会重新拉取最新数据
onShow(() => {
  fetchUserInfo()
})

// 调用后端获取用户信息和统计数据
const fetchUserInfo = async () => {
  try {
    // 对应你写的后端接口：GET /api/user/info
    const res = await request({ 
      url: '/api/user/info', 
      method: 'GET' 
    })
    
    if (res) {
      userInfo.value = res
    }
  } catch (e) {
    console.log("获取用户信息失败，可能未登录", e)
    // 可选：如果未登录，直接跳回登录页
    // uni.reLaunch({ url: '/pages/login/login' })
  }
}

// 处理退出登录
const handleLogout = () => {
  uni.showModal({
    title: '提示',
    content: '确定要退出登录吗？',
    success: (res) => {
      if (res.confirm) {
        // 1. 清除本地存储的 token
        uni.removeStorageSync('token')
        // 如果有存其他的比如 userInfo，也可以一并清除
        uni.removeStorageSync('userInfo')
        
        // 2. 跳转到登录页 (请与 Person C 确认登录页的具体路径)
        uni.reLaunch({ url: '/pages/login/login' }) 
      }
    }
  })
}
</script>

<style scoped>
/* 页面整体背景 */
.container { 
  padding: 20px; 
  background-color: #f5f7fa; 
  min-height: 100vh; 
}

/* 卡片通用样式 (保留了你原本模板里 card 的概念，并做了美化) */
.card {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.04);
}

/* 头部用户信息样式 */
.header-card {
  display: flex;
  align-items: center;
}
.avatar { 
  width: 65px; 
  height: 65px; 
  border-radius: 50%; 
  margin-right: 15px; 
  background: #e0e0e0; 
}
.user-info {
  display: flex;
  flex-direction: column;
}
.username { 
  font-size: 20px; 
  font-weight: bold; 
  color: #333; 
  margin-bottom: 8px;
}
.role-tag { 
  font-size: 12px; 
  color: #fff; 
  padding: 3px 10px; 
  border-radius: 4px; 
  display: inline-block;
}
.admin-tag { background: #ff9800; }
.user-tag { background: #4caf50; }

/* 统计数据区域样式 */
.title {
  font-weight: bold;
  color: #333;
}
.stats-grid { 
  display: flex; 
  justify-content: space-around; 
  padding-top: 10px;
}
.stat-item { 
  display: flex; 
  flex-direction: column; 
  align-items: center; 
}
.num { 
  font-size: 26px; 
  font-weight: bold; 
  margin-bottom: 8px; 
  color: #333;
}
.correct { color: #4caf50; }
.wrong { color: #f44336; }
.label { 
  font-size: 13px; 
  color: #777; 
}

/* 退出登录按钮区域 */
.action-area {
  margin-top: 30px;
}
.logout-btn { 
  background: #ff4d4f; 
  color: #fff; 
  border-radius: 8px; 
  font-size: 16px; 
  border: none; 
}
.logout-btn::after { 
  border: none; 
}
</style>