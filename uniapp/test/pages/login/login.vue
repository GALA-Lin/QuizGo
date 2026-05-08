<template>
  <view class="page">
    <!-- Logo 区域 -->
    <view class="logo-area">
      <text class="logo-icon">📝</text>
      <text class="logo-title">QuizGo</text>
      <text class="logo-sub">随时随地，消灭错题</text>
    </view>

    <!-- Tab 切换：登录 / 注册 -->
    <view class="tab-bar">
      <view class="tab-item" :class="{ active: mode === 'login' }" @click="mode = 'login'">登录</view>
      <view class="tab-item" :class="{ active: mode === 'register' }" @click="mode = 'register'">注册</view>
    </view>

    <!-- 表单卡片 -->
    <view class="card form-card">
      <view class="input-group">
        <text class="input-label">用户名</text>
        <input class="input" v-model="username" placeholder="请输入用户名" placeholder-class="ph" />
      </view>
      <view class="input-group">
        <text class="input-label">密码</text>
        <input class="input" v-model="password" password placeholder="请输入密码" placeholder-class="ph" />
      </view>

      <button class="submit-btn" :loading="loading" :disabled="loading" @click="handleSubmit">
        {{ mode === 'login' ? '登 录' : '注 册' }}
      </button>

      <view class="switch-tip" @click="mode = mode === 'login' ? 'register' : 'login'">
        {{ mode === 'login' ? '还没有账号？点击注册' : '已有账号？点击登录' }}
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { request } from '@/utils/request.js'

const mode = ref('login')
const username = ref('')
const password = ref('')
const loading = ref(false)

const handleSubmit = async () => {
  if (!username.value.trim()) return uni.showToast({ title: '请输入用户名', icon: 'none' })
  if (!password.value.trim()) return uni.showToast({ title: '请输入密码', icon: 'none' })
  loading.value = true
  try {
    mode.value === 'login' ? await doLogin() : await doRegister()
  } finally {
    loading.value = false
  }
}

const doLogin = async () => {
  const res = await request({
    url: '/api/auth/login',
    method: 'POST',
    data: { username: username.value, password: password.value }
  })
  // res = { token, userId, username, role }
  uni.setStorageSync('token', res.token)
  uni.setStorageSync('userInfo', res)
  uni.showToast({ title: '登录成功', icon: 'success' })
  setTimeout(() => uni.switchTab({ url: '/pages/index/index' }), 800)
}

const doRegister = async () => {
  await request({
    url: '/api/auth/register',
    method: 'POST',
    data: { username: username.value, password: password.value }
  })
  uni.showToast({ title: '注册成功，请登录', icon: 'success' })
  mode.value = 'login'
  password.value = ''
}
</script>

<style scoped>
.page {
  min-height: 100vh;
  background: linear-gradient(160deg, #e8f0ff 0%, #f5f7fa 60%);
  padding: 0 30px 40px;
  display: flex;
  flex-direction: column;
  align-items: center;
}
.logo-area {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding-top: 80px;
  margin-bottom: 40px;
}
.logo-icon { font-size: 56px; margin-bottom: 12px; }
.logo-title { font-size: 32px; font-weight: bold; color: #2979ff; letter-spacing: 2px; }
.logo-sub { font-size: 14px; color: #999; margin-top: 6px; }

.tab-bar {
  display: flex;
  background: #e8eaf6;
  border-radius: 30px;
  padding: 4px;
  margin-bottom: 24px;
  width: 100%;
  max-width: 320px;
}
.tab-item {
  flex: 1;
  text-align: center;
  height: 36px;
  line-height: 36px;
  border-radius: 26px;
  font-size: 15px;
  color: #666;
}
.tab-item.active { background: #2979ff; color: #fff; font-weight: bold; }

.form-card { width: 100%; max-width: 360px; border-radius: 16px; padding: 28px 24px; }
.input-group { margin-bottom: 20px; }
.input-label { font-size: 13px; color: #888; display: block; margin-bottom: 8px; }
.input {
  width: 100%;
  height: 44px;
  background: #f5f7fa;
  border-radius: 8px;
  padding: 0 14px;
  font-size: 15px;
  color: #333;
  border: 1.5px solid transparent;
  box-sizing: border-box;
}
.ph { color: #bbb; }
.submit-btn {
  width: 100%;
  height: 48px;
  line-height: 48px;
  background: #2979ff;
  color: #fff;
  border-radius: 10px;
  font-size: 17px;
  font-weight: bold;
  border: none;
  margin-top: 10px;
  letter-spacing: 4px;
}
.submit-btn::after { border: none; }
.submit-btn[disabled] { opacity: 0.6; }
.switch-tip { text-align: center; margin-top: 20px; font-size: 14px; color: #2979ff; }
</style>