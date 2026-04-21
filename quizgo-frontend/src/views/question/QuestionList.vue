<template>
  <div class="page-card">
    <div class="header-actions">
      <h2>题目管理</h2>
      <div class="search-bar">
        <el-input v-model="keyword" placeholder="搜索题目内容" style="width: 200px; margin-right: 10px" clearable @clear="fetchData" />
        <el-button type="primary" @click="fetchData">搜索</el-button>
        <el-button type="success" @click="handleAdd">新增题目</el-button>
      </div>
    </div>

    <el-table :data="tableData" style="width: 100%; margin-top: 20px" border v-loading="loading">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="content" label="题目内容" show-overflow-tooltip />
      <el-table-column prop="type" label="题型" width="100">
        <template #default="scope">
          <el-tag>{{ scope.row.type === 0 ? '单选' : scope.row.type === 1 ? '多选' : '判断' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="answer" label="正确答案" width="100" />
      <el-table-column prop="difficulty" label="难度" width="100">
        <template #default="scope">
          {{ scope.row.difficulty === 1 ? '简单' : scope.row.difficulty === 2 ? '中等' : '困难' }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="scope">
          <el-button size="small" @click="handleEdit(scope.row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
        style="margin-top: 20px; justify-content: flex-end"
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        layout="total, prev, pager, next"
        @current-change="fetchData"
    />

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑题目' : '新增题目'" width="600px">
      <el-form :model="form" label-width="100px" ref="formRef">
        <el-form-item label="所属科目ID" required>
          <el-input-number v-model="form.subjectId" :min="1" />
        </el-form-item>
        <el-form-item label="题目内容" required>
          <el-input v-model="form.content" type="textarea" rows="3" />
        </el-form-item>
        <el-form-item label="题型">
          <el-radio-group v-model="form.type">
            <el-radio :label="0">单选</el-radio>
            <el-radio :label="1">多选</el-radio>
            <el-radio :label="2">判断</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="选项 (JSON)">
          <el-input v-model="form.optionsJson" placeholder='例如: ["A.对","B.错"]' />
        </el-form-item>
        <el-form-item label="正确答案" required>
          <el-input v-model="form.answer" placeholder="单选/判断填字母或A/B，多选如ABCD" />
        </el-form-item>
        <el-form-item label="解析">
          <el-input v-model="form.analysis" type="textarea" />
        </el-form-item>
        <el-form-item label="难度">
          <el-select v-model="form.difficulty">
            <el-option label="简单" :value="1" />
            <el-option label="中等" :value="2" />
            <el-option label="困难" :value="3" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import request from '../../utils/request.js' // 请确保路径正确
import { ElMessage, ElMessageBox } from 'element-plus'

const tableData = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const keyword = ref('')
const loading = ref(false)
const dialogVisible = ref(false)

const form = reactive({
  id: null,
  subjectId: 1,
  content: '',
  type: 0,
  optionsJson: '["A.","B.","C.","D."]',
  answer: '',
  analysis: '',
  difficulty: 1
})

const fetchData = async () => {
  loading.value = true
  try {
    const res = await request.get('/api/admin/question/list', {
      params: {
        subjectId: 1, // 这里暂时硬编码为科目1，后续可扩展科目筛选
        page: currentPage.value,
        size: pageSize.value,
        keyword: keyword.value
      }
    })
    if (res.code === 200) {
      tableData.value = res.data.records
      total.value = res.data.total
    }
  } catch (error) {
    console.error('获取题目失败', error)
  } finally {
    loading.value = false
  }
}

const handleAdd = () => {
  Object.assign(form, {
    id: null,
    subjectId: 1,
    content: '',
    type: 0,
    optionsJson: '["A.","B.","C.","D."]',
    answer: '',
    analysis: '',
    difficulty: 1
  })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  Object.assign(form, row)
  dialogVisible.value = true
}

const submitForm = async () => {
  try {
    const url = form.id ? '/api/admin/question/update' : '/api/admin/question/add'
    const method = form.id ? 'put' : 'post'
    const res = await request[method](url, form)
    if (res.code === 200) {
      ElMessage.success(form.id ? '修改成功' : '新增成功')
      dialogVisible.value = false
      fetchData()
    }
  } catch (error) {
    console.error('提交失败', error)
  }
}

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除这道题目吗？', '警告', { type: 'warning' })
    const res = await request.delete(`/api/admin/question/${id}`)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      fetchData()
    }
  } catch (error) {}
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.page-card {
  padding: 24px;
  background: #fff;
  border-radius: 8px;
}
.header-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
.search-bar {
  display: flex;
  align-items: center;
}
</style>