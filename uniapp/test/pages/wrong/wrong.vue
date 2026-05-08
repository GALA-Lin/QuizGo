<template>
  <view class="container">
    <!-- 错题本页面（D 负责） -->
    
    <!-- 头部标题区 -->
    <view class="header">
      <text class="title">我的错题本</text>
      <text class="subtext">温故而知新，消灭错题！</text>
    </view>

    <!-- 列表为空时的提示 -->
    <view v-if="wrongList.length === 0" class="empty-state">
      <text class="empty-text">太棒了，目前没有错题！</text>
    </view>
    
    <!-- 错题列表展示区 -->
    <view 
      class="card wrong-card" 
      v-for="item in wrongList" 
      :key="item.wrongId" 
      @click="goToDetail(item)"
    >
      <view class="card-header">
        <text class="subject-tag">{{ item.subjectName }}</text>
        <!-- 直接使用你后端格式化好的 addTime -->
        <text class="date">{{ item.addTime }}</text>
      </view>
      
      <view class="content">
        <!-- 题目类型标识：0单选 1多选 2判断 -->
        <text class="type-badge">[{{ item.type === 0 ? '单选' : (item.type === 1 ? '多选' : '判断') }}]</text>
        <text class="question-text">{{ item.content }}</text>
      </view>
      
      <view class="card-footer">
        <text class="hint">点击重做该题</text>
        <!-- 使用 .stop 阻止点击事件冒泡，避免触发 goToDetail 跳转 -->
        <button class="del-btn" @click.stop="deleteWrong(item.wrongId)">移出</button>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { request } from '@/utils/request.js'

const wrongList = ref([])

// 每次进入页面都刷新列表
onShow(() => {
  fetchWrongList()
})

// 获取错题列表数据
const fetchWrongList = async () => {
  try {
    const res = await request({ 
      url: '/api/wrong/list', 
      method: 'GET',
      data: { page: 1, size: 50 } // 这里你可以根据需要调整分页
    })
    // 你的后端返回的是 Page 对象，数据在 records 里面
    if (res && res.records) {
      wrongList.value = res.records
    }
  } catch (e) {
    uni.showToast({ title: '获取列表失败', icon: 'none' })
  }
}

// 删除错题
const deleteWrong = (id) => {
  uni.showModal({
    title: '确认移出',
    content: '这道错题已掌握了吗？',
    success: async (res) => {
      if (res.confirm) {
        try {
          await request({ 
            url: `/api/wrong/${id}`, 
            method: 'DELETE' 
          })
          uni.showToast({ title: '已移出错题本', icon: 'success' })
          fetchWrongList() // 重新拉取列表
        } catch (e) {
          uni.showToast({ title: '删除失败', icon: 'none' })
        }
      }
    }
  })
}

// 跳转到错题详情/重做页
const goToDetail = (item) => {
  // 把对象存到缓存里传递，避免 URL 传参太长报错
  uni.setStorageSync('currentWrongQuestion', item)
  uni.navigateTo({ url: '/pages/wrong/wrong_detail' })
}
</script>

<style scoped>
.container { padding: 20px; background-color: #f5f7fa; min-height: 100vh; }
.header { margin-bottom: 20px; }
.title { font-size: 24px; font-weight: bold; color: #333; display: block;}
.subtext { font-size: 14px; color: #888; margin-top: 5px; display: block;}

.empty-state { text-align: center; margin-top: 80px; }
.empty-text { color: #999; font-size: 16px; }

.card { background: #fff; border-radius: 12px; padding: 20px; margin-bottom: 15px; box-shadow: 0 2px 8px rgba(0,0,0,0.04); }
.wrong-card { transition: all 0.2s; }
.wrong-card:active { transform: scale(0.98); }

.card-header { display: flex; justify-content: space-between; margin-bottom: 12px; align-items: center;}
.subject-tag { background: #e6f7ff; color: #1890ff; padding: 4px 10px; border-radius: 6px; font-size: 12px; font-weight: bold;}
.date { font-size: 13px; color: #a0a0a0; }

.content { font-size: 16px; margin-bottom: 15px; line-height: 1.6; color: #333;}
.type-badge { color: #ff9800; font-weight: bold; margin-right: 6px; }

.card-footer { display: flex; justify-content: space-between; align-items: center; border-top: 1px solid #f0f0f0; padding-top: 15px; margin-top: 5px;}
.hint { font-size: 13px; color: #1890ff; }
.del-btn { margin: 0; font-size: 13px; background: #fff0f6; color: #f5222d; border-radius: 20px; padding: 0 15px; height: 30px; line-height: 30px; border: none;}
.del-btn::after { border: none; }
</style>