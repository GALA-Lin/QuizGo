<template>
  <view class="container">
    <view class="card">
      <view class="title">首页（C 负责）</view>
      <view class="subtext">
        这里给 C 展示科目列表。
        接口：GET /api/subject/list
      </view>
      <view class="btn" @click="goQuiz">占位跳转刷题页</view>
    </view>
  </view>
</template>

<script setup>
const goQuiz = () => {
  uni.navigateTo({ url: '/pages/quiz/quiz?subjectId=1' })
}
</script>
