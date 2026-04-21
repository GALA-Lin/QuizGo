<template>
  <view class="container">
    <view class="card">
      <view class="title">登录 / 注册（C 负责）</view>
      <view class="subtext">
        这里给 C 写登录和注册逻辑。
        登录接口：POST /api/user/login
        注册接口：POST /api/user/register
      </view>
      <view class="btn" @click="goHome">占位跳转首页</view>
    </view>
  </view>
</template>

<script setup>
const goHome = () => {
  uni.switchTab({ url: '/pages/index/index' })
}
</script>
