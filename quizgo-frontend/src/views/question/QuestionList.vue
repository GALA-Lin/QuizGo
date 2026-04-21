<template>
  <div class="question-page">
    <header class="page-head">
      <div class="head-left">
        <div class="head-icon">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none">
            <path d="M9 12h6M9 16h6M9 8h6M5 4h14a1 1 0 0 1 1 1v14a1 1 0 0 1-1 1H5a1 1 0 0 1-1-1V5a1 1 0 0 1 1-1z" stroke="currentColor" stroke-width="1.8" stroke-linecap="round"/>
          </svg>
        </div>
        <div>
          <h1>题目管理</h1>
          <p>维护题库内容、选项、答案与解析</p>
        </div>
      </div>

      <div class="head-right">
        <el-upload
            class="upload-excel"
            action="http://localhost:8081/api/admin/question/import"
            :headers="uploadHeaders"
            :data="uploadData"
            :show-file-list="false"
            :before-upload="beforeUpload"
            :on-success="handleUploadSuccess"
            :on-error="handleUploadError"
            accept=".xlsx, .xls"
        >
          <el-button type="success" class="import-btn" :disabled="!currentBankId" plain>
            <el-icon><Upload /></el-icon>
            <span>批量导入 Excel</span>
          </el-button>
        </el-upload>

        <el-button type="primary" class="add-btn" @click="handleAdd">
          <el-icon><Plus /></el-icon>
          <span>新增题目</span>
        </el-button>
      </div>
    </header>

    <section class="toolbar-bar">
      <div class="toolbar-left">
        <el-select
            v-model="currentBankId"
            placeholder="请选择目标题库"
            style="width: 220px"
            clearable
            @change="fetchData"
        >
          <el-option label="查看全部题目" :value="null" />
          <el-option
              v-for="bank in bankList"
              :key="bank.id"
              :label="bank.name"
              :value="bank.id"
          />
        </el-select>

        <el-button type="primary" plain @click="bankDialogVisible = true">
          + 新建题库
        </el-button>

        <el-button
            v-if="currentBankId"
            type="danger"
            plain
            @click="handleDeleteBank"
        >
          <el-icon><Delete /></el-icon>
          删除当前题库
        </el-button>
        <el-input
            v-model="keyword"
            placeholder="搜索题目内容…"
            clearable
            class="toolbar-search"
            @keyup.enter="fetchData"
            @clear="fetchData"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-button class="query-btn" @click="fetchData">查询</el-button>
      </div>
      <span class="data-count">共 <em>{{ total }}</em> 条题目</span>
    </section>

    <section class="table-card">
      <el-table :data="tableData" v-loading="loading" class="question-table">
        <el-table-column prop="id" label="ID" width="72" />
        <el-table-column prop="content" label="题目内容" min-width="340" show-overflow-tooltip />
        <el-table-column label="题型" width="100">
          <template #default="scope">
            <span class="type-badge" :class="'type-' + scope.row.type">
              {{ typeMap[scope.row.type] }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="answer" label="答案" width="90" align="center" />
        <el-table-column label="操作" width="140" align="right">
          <template #default="scope">
            <div class="action-group">
              <button class="act-btn act-edit" @click="handleEdit(scope.row)">编辑</button>
              <button class="act-btn act-del" @click="handleDelete(scope.row.id)">删除</button>
            </div>
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

    <el-dialog v-model="bankDialogVisible" title="新建题库容器" width="420px" class="question-dialog">
      <el-form :model="bankForm" label-position="top">
        <el-form-item label="题库名称" required>
          <el-input v-model="bankForm.name" placeholder="例如：计网期末押题集" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="bankForm.description" type="textarea" placeholder="备注该题库的用途" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <button class="footer-cancel" @click="bankDialogVisible = false">取消</button>
          <button class="footer-save" @click="submitBank">立即创建</button>
        </div>
      </template>
    </el-dialog>

    <el-dialog
        v-model="dialogVisible"
        :title="form.id ? '编辑题目' : '新增题目'"
        width="680px"
        class="question-dialog"
    >
      <el-form :model="form" label-position="top" class="question-form">
        <div class="grid-two">
          <el-form-item label="所属科目 ID">
            <el-input-number v-model="form.subjectId" :min="1" style="width: 100%" />
          </el-form-item>
          <el-form-item label="题型">
            <el-select v-model="form.type" style="width: 100%" @change="handleTypeChange">
              <el-option label="单选题" :value="0" />
              <el-option label="多选题" :value="1" />
              <el-option label="判断题" :value="2" />
            </el-select>
          </el-form-item>
        </div>

        <el-form-item label="题目内容">
          <el-input
              v-model="form.content"
              type="textarea"
              :rows="4"
              resize="none"
              placeholder="请输入题目内容"
          />
        </el-form-item>

        <div v-if="form.type !== 2" class="option-block">
          <div class="block-head">
            <span>选项设置</span>
            <button class="add-option-btn" @click="addOption">＋ 添加选项</button>
          </div>
          <div v-for="(opt, index) in optionsList" :key="index" class="option-row">
            <div class="option-label">{{ String.fromCharCode(65 + index) }}</div>
            <el-input v-model="optionsList[index]" placeholder="请输入选项内容" />
            <button
                class="rm-btn"
                @click="removeOption(index)"
                :disabled="optionsList.length <= 2"
            >✕</button>
          </div>
        </div>

        <el-form-item label="正确答案">
          <el-radio-group v-if="form.type === 0" v-model="form.answer" class="answer-group">
            <el-radio
                v-for="(opt, i) in optionsList"
                :key="i"
                :label="String.fromCharCode(65 + i)"
            >{{ String.fromCharCode(65 + i) }}</el-radio>
          </el-radio-group>

          <el-checkbox-group v-else-if="form.type === 1" v-model="multiAnswers" class="answer-group">
            <el-checkbox
                v-for="(opt, i) in optionsList"
                :key="i"
                :label="String.fromCharCode(65 + i)"
            >{{ String.fromCharCode(65 + i) }}</el-checkbox>
          </el-checkbox-group>

          <el-radio-group v-else v-model="form.answer" class="answer-group">
            <el-radio label="正确">正确</el-radio>
            <el-radio label="错误">错误</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="解析说明">
          <el-input
              v-model="form.analysis"
              type="textarea"
              :rows="3"
              resize="none"
              placeholder="请输入答案解析（选填）"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <div class="dialog-footer">
          <button class="footer-cancel" @click="dialogVisible = false">取消</button>
          <button class="footer-save" @click="submitForm">保存题目</button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { Plus, Search, Upload, Delete } from '@element-plus/icons-vue' // 引入 Delete 图标
import request from '../../utils/request.js'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const dialogVisible = ref(false)
const tableData = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const keyword = ref('')

const typeMap = { 0: '单选', 1: '多选', 2: '判断' }

// --- 题库容器相关逻辑 ---
const bankList = ref([])
const currentBankId = ref(null)
const bankDialogVisible = ref(false)
const bankForm = reactive({ name: '', description: '', subjectId: 1 })

const fetchBankList = async () => {
  try {
    const res = await request.get('/api/admin/bank/list', { params: { subjectId: 1 } })
    if (res.code === 200) bankList.value = res.data
  } catch (err) { console.error('拉取题库失败', err) }
}

const submitBank = async () => {
  if (!bankForm.name) return ElMessage.warning('名称不能为空')
  try {
    const res = await request.post('/api/admin/bank/add', bankForm)
    if (res.code === 200) {
      ElMessage.success('题库创建成功')
      bankDialogVisible.value = false
      bankForm.name = ''; bankForm.description = ''
      fetchBankList()
    }
  } catch (err) { console.error(err) }
}

// 👇 新增：删除题库方法 👇
const handleDeleteBank = async () => {
  const selectedBank = bankList.value.find(b => b.id === currentBankId.value)
  try {
    await ElMessageBox.confirm(
        `确定要删除题库【${selectedBank?.name}】吗？删除后该题库下的所有题目将永久消失！`,
        '警告',
        {
          confirmButtonText: '确定删除',
          cancelButtonText: '点错了',
          type: 'error',
        }
    )

    const res = await request.delete(`/api/admin/bank/${currentBankId.value}`)
    if (res.code === 200) {
      ElMessage.success('题库及其所属题目已清空')
      currentBankId.value = null // 清空选中
      fetchBankList() // 刷新下拉列表
      fetchData()     // 刷新表格
    }
  } catch (err) {
    // 用户取消了操作
  }
}
// 👆 结束 👆

// --- 批量导入逻辑 ---
const token = localStorage.getItem('token') || ''
const uploadHeaders = { Authorization: `Bearer ${token}` }

const uploadData = computed(() => ({
  subjectId: 1,
  bankId: currentBankId.value
}))

const beforeUpload = (file) => {
  if (!currentBankId.value) {
    ElMessage.warning('请先在下拉框选择一个目标题库！')
    return false
  }
  const isExcel = file.type === 'application/vnd.ms-excel' ||
      file.type === 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
  if (!isExcel) {
    ElMessage.error('只能上传 Excel 文件!')
    return false
  }
  ElMessage.info('正在解析并导入，请稍候...')
  return true
}

const handleUploadSuccess = (res) => {
  if (res.code === 200) { ElMessage.success('🎉 批量导入成功！'); fetchData() }
  else ElMessage.error(res.msg || '导入失败')
}

const handleUploadError = () => ElMessage.error('无法连接到后端 8081 端口')

// --- 题目业务逻辑 ---
const form = reactive({
  id: null,
  subjectId: 1,
  bankId: null,
  content: '',
  type: 0,
  answer: '',
  analysis: '',
  difficulty: 1,
  optionsJson: ''
})

const optionsList = ref(['', '', '', ''])
const multiAnswers = ref([])

const handleTypeChange = (newType) => {
  form.answer = ''
  multiAnswers.value = []
  if (newType === 2) {
    optionsList.value = ['正确', '错误']
  } else if (optionsList.value.length < 2) {
    optionsList.value = ['', '', '', '']
  }
}

const addOption = () => { optionsList.value.push('') }
const removeOption = (index) => { optionsList.value.splice(index, 1) }

const fetchData = async () => {
  loading.value = true
  try {
    const res = await request.get('/api/admin/question/list', {
      params: {
        subjectId: 1,
        bankId: currentBankId.value,
        page: currentPage.value,
        size: pageSize.value,
        keyword: keyword.value
      }
    })
    if (res.code === 200) {
      tableData.value = res.data.records
      total.value = res.data.total
    }
  } catch (err) { console.error(err) }
  finally { loading.value = false }
}

const handleAdd = () => {
  Object.assign(form, { id: null, subjectId: 1, content: '', type: 0, answer: '', analysis: '', difficulty: 1, optionsJson: '' })
  optionsList.value = ['', '', '', '']
  multiAnswers.value = []
  dialogVisible.value = true
}

const handleEdit = (row) => {
  Object.assign(form, row)
  try { optionsList.value = JSON.parse(row.optionsJson || '[]') } catch (e) { optionsList.value = [] }
  if (form.type === 1) { multiAnswers.value = form.answer ? form.answer.split('') : [] }
  dialogVisible.value = true
}

const submitForm = async () => {
  if (form.type === 1) { form.answer = multiAnswers.value.sort().join('') }
  const formattedOptions = optionsList.value.map((val, idx) => {
    const prefix = String.fromCharCode(65 + idx)
    return val.startsWith(prefix + '.') ? val : `${prefix}.${val}`
  })
  form.optionsJson = JSON.stringify(formattedOptions)
  form.bankId = currentBankId.value

  const method = form.id ? 'put' : 'post'
  const url = form.id ? '/api/admin/question/update' : '/api/admin/question/add'
  try {
    const res = await request[method](url, form)
    if (res.code === 200) {
      ElMessage.success('操作成功')
      dialogVisible.value = false
      fetchData()
    }
  } catch (err) { console.error(err) }
}

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('题目删除后无法恢复，确定继续？', '提示', { type: 'warning' })
    const res = await request.delete(`/api/admin/question/${id}`)
    if (res.code === 200) { ElMessage.success('删除成功'); fetchData() }
  } catch (err) { }
}

onMounted(() => {
  fetchBankList()
  fetchData()
})
</script>

<style scoped>
/* ── 基础变量 ── */
.question-page {
  --brand: #4f6ef7;
  --brand-dark: #3a56e0;
  --brand-light: #eef1fe;
  --danger: #f5533d;
  --danger-light: #fff1f0;
  --success: #12b76a;
  --warn: #f79009;

  --bg: #f3f4f8;
  --card: #ffffff;
  --border: #e8eaf0;
  --text-primary: #1a1d2e;
  --text-secondary: #6b7280;
  --text-muted: #9ca3af;

  --radius: 14px;
  --radius-sm: 8px;
  --shadow: 0 1px 3px rgba(0,0,0,.06), 0 4px 16px rgba(0,0,0,.04);
  --shadow-md: 0 4px 24px rgba(0,0,0,.09);

  padding: 32px 36px;
  background: var(--bg);
  min-height: 100vh;
  font-family: 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', sans-serif;
}

/* ── 顶部 Header ── */
.page-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
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
  flex-shrink: 0;
  box-shadow: 0 4px 12px rgba(79,110,247,.35);
}

.page-head h1 {
  margin: 0;
  font-size: 22px;
  font-weight: 700;
  color: var(--text-primary);
  letter-spacing: -0.01em;
  line-height: 1.2;
}

.page-head p {
  margin: 3px 0 0;
  font-size: 13px;
  color: var(--text-muted);
}

.head-right {
  display: flex;
  gap: 12px;
}

.import-btn {
  border-radius: 10px !important;
  height: 40px !important;
  font-weight: 600 !important;
}

.add-btn {
  background: var(--brand) !important;
  border-color: var(--brand) !important;
  border-radius: 10px !important;
  padding: 0 20px !important;
  height: 40px !important;
  font-size: 14px !important;
  font-weight: 600 !important;
  box-shadow: 0 3px 10px rgba(79,110,247,.30) !important;
  transition: transform .15s, box-shadow .15s !important;
}
.add-btn:hover {
  background: var(--brand-dark) !important;
  transform: translateY(-1px) !important;
  box-shadow: 0 5px 16px rgba(79,110,247,.40) !important;
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

.toolbar-left {
  display: flex;
  gap: 12px;
  align-items: center;
}

.toolbar-search {
  width: 260px;
}

:deep(.toolbar-search .el-input__wrapper) {
  border-radius: 9px !important;
  border-color: var(--border) !important;
  box-shadow: none !important;
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
  transition: all .15s;
}
.query-btn:hover {
  border-color: var(--brand);
  color: var(--brand);
  background: var(--brand-light);
}

.data-count {
  font-size: 13px;
  color: var(--text-muted);
}
.data-count em {
  font-style: normal;
  font-weight: 700;
  color: var(--brand);
}

/* ── 表格卡片 ── */
.table-card {
  background: var(--card);
  border: 1px solid var(--border);
  border-radius: var(--radius);
  overflow: hidden;
  box-shadow: var(--shadow);
}

.question-table {
  width: 100%;
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

/* 题型徽章 */
.type-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 3px 10px;
  border-radius: 20px;
  font-size: 11.5px;
  font-weight: 600;
}

.type-0 { color: var(--brand); background: var(--brand-light); }
.type-1 { color: var(--success); background: #e8faf3; }
.type-2 { color: var(--warn); background: #fff8ec; }

/* 操作按钮 */
.action-group {
  display: flex;
  justify-content: flex-end;
  gap: 6px;
}

.act-btn {
  height: 28px;
  padding: 0 12px;
  border-radius: 7px;
  font-size: 12.5px;
  font-weight: 500;
  cursor: pointer;
  border: 1px solid transparent;
  transition: all .15s;
}

.act-edit { color: var(--brand); background: var(--brand-light); }
.act-del { color: var(--danger); background: var(--danger-light); }

/* 分页 */
.table-footer {
  display: flex;
  justify-content: flex-end;
  padding: 14px 20px 16px;
  border-top: 1px solid #f3f4f8;
}

/* ── Dialog ── */
:deep(.el-dialog) {
  border-radius: 18px !important;
  overflow: hidden;
}

:deep(.el-dialog__header) {
  padding: 22px 28px 16px !important;
  border-bottom: 1px solid var(--border);
  margin: 0 !important;
}

:deep(.el-dialog__title) {
  font-size: 17px !important;
  font-weight: 700 !important;
}

/* ── Form ── */
.grid-two {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.option-block {
  margin-bottom: 20px;
  padding: 16px 18px;
  background: #f8f9fc;
  border: 1px solid var(--border);
  border-radius: 12px;
}

.block-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 14px;
  font-size: 13px;
  font-weight: 600;
  color: var(--text-secondary);
}

.option-row {
  display: grid;
  grid-template-columns: 30px 1fr 30px;
  gap: 10px;
  align-items: center;
  margin-bottom: 10px;
}

.option-label {
  width: 30px;
  height: 30px;
  border-radius: 9px;
  background: linear-gradient(135deg, var(--brand) 0%, #7c91fa 100%);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: 700;
}

.rm-btn {
  width: 28px;
  height: 28px;
  border-radius: 7px;
  border: 1px solid #fcd5d0;
  background: var(--danger-light);
  color: var(--danger);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 底部按钮 */
.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.footer-cancel {
  height: 38px;
  padding: 0 20px;
  border-radius: 9px;
  border: 1px solid var(--border);
  background: #fff;
  cursor: pointer;
}

.footer-save {
  height: 38px;
  padding: 0 22px;
  border-radius: 9px;
  border: none;
  background: linear-gradient(135deg, var(--brand) 0%, #7c91fa 100%);
  color: #fff;
  font-weight: 600;
  cursor: pointer;
  box-shadow: 0 3px 10px rgba(79,110,247,.30);
}

/* ── 响应式 ── */
@media (max-width: 768px) {
  .page-head, .toolbar-bar { flex-direction: column; align-items: stretch; gap: 12px; }
  .toolbar-left { flex-direction: column; align-items: stretch; }
  .toolbar-search { width: 100%; }
}
</style>