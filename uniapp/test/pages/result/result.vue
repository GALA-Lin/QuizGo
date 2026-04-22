<template>
  <view class="page">
    <view class="header">结果页（C可选）</view>
    <view class="card">
      <view>这里可以展示答题结果、正确答案、解析。</view>
    </view>
  </view>
</template>

<script>
export default {}
</script>

<style scoped>
.page {
  min-height: 100vh;
  padding: 30rpx;
}
.header {
  font-size: 40rpx;
  font-weight: bold;
  margin-bottom: 20rpx;
}
.card {
  background: #fff;
  border-radius: 16rpx;
  padding: 24rpx;
}
</style>
