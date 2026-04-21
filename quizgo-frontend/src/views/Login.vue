<template>
  <div class="login-wrapper">
    <div class="login-card">
      <div class="login-header">
        <div class="brand-logo">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none">
            <path d="M12 2L2 7l10 5 10-5-10-5zM2 17l10 5 10-5M2 12l10 5 10-5" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </div>
        <h2>QuizGo Admin</h2>
        <p>欢迎回来，请登录管理后台</p>
      </div>

      <el-form :model="form" :rules="rules" ref="loginFormRef" @keyup.enter="handleLogin">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="请输入账号 (如: admin)" size="large" class="shadcn-input" />
        </el-form-item>

        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" show-password size="large" class="shadcn-input" />
        </el-form-item>

        <el-button type="primary" class="login-btn" :loading="loading" @click="handleLogin">
          登 录
        </el-button>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '../utils/request.js'

const router = useRouter()
const loginFormRef = ref(null)
const loading = ref(false)

const form = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = async () => {
  if (!loginFormRef.value) return

  await loginFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const res = await request.post('/api/auth/login', form)
        if (res.code === 200) {
          ElMessage.success('登录成功')
          // 存储 Token 和用户信息
          localStorage.setItem('token', res.data.token)
          localStorage.setItem('userInfo', JSON.stringify(res.data))

          // 跳转到后台首页
          router.push('/')
        } else {
          ElMessage.error(res.message || '账号或密码错误')
        }
      } catch (err) {
        console.error('登录异常', err)
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped>
.login-wrapper {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f3f4f8;
  font-family: 'PingFang SC', sans-serif;
}

.login-card {
  width: 100%;
  max-width: 380px;
  background: #fff;
  padding: 40px;
  border-radius: 16px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.08);
  border: 1px solid #e8eaf0;
}

.login-header {
  text-align: center;
  margin-bottom: 32px;
}

.brand-logo {
  width: 48px;
  height: 48px;
  margin: 0 auto 16px;
  background: linear-gradient(135deg, #4f6ef7 0%, #7c91fa 100%);
  color: #fff;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 12px rgba(79, 110, 247, 0.3);
}

.login-header h2 {
  margin: 0 0 8px;
  font-size: 24px;
  font-weight: 700;
  color: #1a1d2e;
}

.login-header p {
  margin: 0;
  font-size: 14px;
  color: #6b7280;
}

:deep(.shadcn-input .el-input__wrapper) {
  box-shadow: 0 0 0 1px #e4e4e7 inset;
  border-radius: 8px;
  padding: 4px 12px;
}

:deep(.shadcn-input .el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 2px #4f6ef7 inset !important;
}

.login-btn {
  width: 100%;
  margin-top: 10px;
  height: 44px;
  font-size: 15px;
  font-weight: 600;
  border-radius: 8px;
  background: #1a1d2e;
  border: none;
  transition: all 0.2s;
}

.login-btn:hover {
  background: #2d3142;
  transform: translateY(-1px);
}
</style>