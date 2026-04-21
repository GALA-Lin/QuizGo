<template>
  <div class="user-page">
    <header class="page-head">
      <div class="head-left">
        <div class="head-icon">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none">
            <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2M12 11a4 4 0 1 0 0-8 4 4 0 0 0 0 8z" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </div>
        <div>
          <h1>用户管理</h1>
          <p>查看注册用户列表及权限状态</p>
        </div>
      </div>
    </header>

    <section class="toolbar-bar">
      <div class="toolbar-left">
        <span class="data-count">系统共注册 <em>{{ total }}</em> 名用户</span>
      </div>
      <el-button class="query-btn" @click="fetchData">刷新数据</el-button>
    </section>

    <section class="table-card">
      <el-table :data="tableData" v-loading="loading" class="user-table">
        <el-table-column prop="id" label="用户 ID" width="100" />
        <el-table-column prop="username" label="用户名" min-width="200" />
        <el-table-column label="角色权限" width="150">
          <template #default="scope">
            <span class="role-badge" :class="scope.row.role === 1 ? 'role-admin' : 'role-user'">
              {{ scope.row.role === 1 ? '管理员' : '普通用户' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="注册时间" width="220">
          <template #default="scope">
            {{ scope.row.createTime ? scope.row.createTime.replace('T', ' ') : '-' }}
          </template>
        </el-table-column>
      </el-table>

      <div class="table-footer">
        <el-pagination
            v-model:current-page="currentPage"
            :page-size="pageSize"
            :total="total"
            layout="total, prev, pager, next"
            @current-change="fetchData"
        />
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '../../utils/request.js'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)

const fetchData = async () => {
  loading.value = true
  try {
    const res = await request.get('/api/admin/user/list', {
      params: {
        page: currentPage.value,
        size: pageSize.value
      }
    })
    if (res.code === 200) {
      tableData.value = res.data.records
      total.value = res.data.total
    } else {
      ElMessage.error(res.message || '获取用户列表失败')
    }
  } catch (err) {
    console.error(err)
  } finally {
    loading.value = false
  }
}

onMounted(fetchData)
</script>

<style scoped>
/* ── 基础变量 ── */
.user-page {
  --brand: #4f6ef7;
  --brand-dark: #3a56e0;
  --brand-light: #eef1fe;
  --bg: #f3f4f8;
  --card: #ffffff;
  --border: #e8eaf0;
  --text-primary: #1a1d2e;
  --text-secondary: #6b7280;
  --text-muted: #9ca3af;
  --radius: 14px;
  --shadow: 0 1px 3px rgba(0,0,0,.06), 0 4px 16px rgba(0,0,0,.04);

  padding: 32px 36px;
  background: var(--bg);
  min-height: 100vh;
}

/* ── 顶部 Header ── */
.page-head {
  display: flex;
  align-items: center;
  margin-bottom: 24px;
}

.head-left {
  display: flex;
  align-items: center;
  gap: 14px;
}

.head-icon {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  background: linear-gradient(135deg, var(--brand) 0%, #7c91fa 100%);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 12px rgba(79,110,247,.35);
}

.page-head h1 {
  margin: 0;
  font-size: 22px;
  font-weight: 700;
  color: var(--text-primary);
}

.page-head p {
  margin: 3px 0 0;
  font-size: 13px;
  color: var(--text-muted);
}

/* ── 工具栏 ── */
.toolbar-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: var(--card);
  border: 1px solid var(--border);
  border-radius: var(--radius);
  padding: 14px 20px;
  margin-bottom: 16px;
  box-shadow: var(--shadow);
}

.data-count {
  font-size: 14px;
  color: var(--text-secondary);
}
.data-count em {
  font-style: normal;
  font-weight: 700;
  color: var(--brand);
}

.query-btn {
  height: 34px;
  padding: 0 18px;
  border-radius: 9px;
  border: 1px solid var(--border);
  background: var(--card);
  color: var(--text-primary);
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
}
.query-btn:hover {
  border-color: var(--brand);
  color: var(--brand);
  background: var(--brand-light);
}

/* ── 表格 ── */
.table-card {
  background: var(--card);
  border: 1px solid var(--border);
  border-radius: var(--radius);
  overflow: hidden;
  box-shadow: var(--shadow);
}

:deep(.el-table th.el-table__cell) {
  background: #f8f9fc !important;
  color: var(--text-secondary) !important;
  font-weight: 600 !important;
  font-size: 12px !important;
  border-bottom: 1px solid var(--border) !important;
  padding: 13px 0 !important;
}

:deep(.el-table td.el-table__cell) {
  padding: 15px 0 !important;
  border-bottom-color: #f3f4f8 !important;
  color: var(--text-primary);
  font-size: 14px;
}

.role-badge {
  display: inline-flex;
  padding: 3px 10px;
  border-radius: 20px;
  font-size: 11.5px;
  font-weight: 600;
}
.role-admin { color: #f79009; background: #fff8ec; }
.role-user { color: var(--brand); background: var(--brand-light); }

.table-footer {
  display: flex;
  justify-content: flex-end;
  padding: 14px 20px 16px;
  border-top: 1px solid #f3f4f8;
}

:deep(.el-pagination.is-background .el-pager li.is-active) {
  background: var(--brand) !important;
}
</style>