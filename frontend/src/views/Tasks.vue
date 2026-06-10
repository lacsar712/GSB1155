<template>
  <div class="tasks-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>任务管理</span>
          <el-button 
            type="primary" 
            @click="openCreateDialog"
            v-if="userStore.isAdmin || userStore.isManager"
          >
            <el-icon><Plus /></el-icon>
            新建任务
          </el-button>
        </div>
      </template>
      
      <!-- 筛选条件 -->
      <div class="filter-section">
        <el-form :inline="true" :model="filterForm">
          <el-form-item label="状态">
            <el-select v-model="filterForm.status" placeholder="全部状态" clearable style="width: 120px">
              <el-option label="待执行" :value="0" />
              <el-option label="执行中" :value="1" />
              <el-option label="待审核" :value="2" />
              <el-option label="已完成" :value="3" />
              <el-option label="已驳回" :value="4" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="fetchTasks">
              <el-icon><Search /></el-icon>
              搜索
            </el-button>
            <el-button @click="resetFilter">
              <el-icon><Refresh /></el-icon>
              重置
            </el-button>
          </el-form-item>
        </el-form>
      </div>
      
      <!-- 任务列表 -->
      <el-table 
        :data="taskList" 
        v-loading="loading"
        stripe
        border
        style="width: 100%"
      >
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="title" label="任务名称" min-width="200" show-overflow-tooltip />
        <el-table-column prop="orgName" label="执行单位" width="140" show-overflow-tooltip />
        <el-table-column prop="executorName" label="执行人" width="100" />
        <el-table-column label="优先级" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="getPriorityType(row.priority)" size="small">
              {{ row.priorityName }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">
              {{ row.statusName }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="截止时间" width="160">
          <template #default="{ row }">
            <span :class="{ 'text-danger': isOverdue(row.deadline, row.status) }">
              {{ formatDate(row.deadline) }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="260" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="viewTask(row)">
              查看
            </el-button>
            <el-button 
              type="success" 
              link 
              size="small" 
              v-if="row.status === 0"
              @click="startTask(row)"
            >
              开始执行
            </el-button>
            <el-button 
              type="warning" 
              link 
              size="small" 
              v-if="row.status === 1"
              @click="openReportDialog(row)"
            >
              提交巡检上报
            </el-button>
            <el-button
              type="warning"
              link
              size="small"
              v-if="(userStore.isAdmin || userStore.isManager) && row.status === 2"
              @click="openReviewDialog(row)"
            >
              审核上报
            </el-button>
            <el-button 
              type="danger" 
              link 
              size="small" 
              v-if="(userStore.isAdmin || userStore.isManager) && row.status < 3"
              @click="deleteTask(row)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
      <div class="pagination-section">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.size"
          :total="pagination.total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="fetchTasks"
          @current-change="fetchTasks"
        />
      </div>
    </el-card>
    
    <!-- 新建/编辑任务对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑任务' : '新建任务'"
      width="600px"
      destroy-on-close
    >
      <el-form 
        ref="formRef"
        :model="taskForm" 
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="任务标题" prop="title">
          <el-input v-model="taskForm.title" placeholder="请输入任务标题" />
        </el-form-item>
        
        <el-form-item label="任务描述" prop="description">
          <el-input 
            v-model="taskForm.description" 
            type="textarea" 
            :rows="3"
            placeholder="请输入任务描述"
          />
        </el-form-item>
        
        <el-form-item label="检查模板" prop="templateId">
          <el-select v-model="taskForm.templateId" placeholder="请选择模板" style="width: 100%">
            <el-option
              v-for="item in templates"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        
        <el-form-item label="执行单位" prop="orgId">
          <el-select v-model="taskForm.orgId" placeholder="请选择单位" style="width: 100%" @change="onOrgChange">
            <el-option
              v-for="item in organizations"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        
        <el-form-item label="执行人" prop="executorId">
          <el-select v-model="taskForm.executorId" placeholder="请选择执行人" style="width: 100%">
            <el-option
              v-for="item in inspectors"
              :key="item.id"
              :label="item.realName"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        
        <el-form-item label="优先级" prop="priority">
          <el-radio-group v-model="taskForm.priority">
            <el-radio :label="1">低</el-radio>
            <el-radio :label="2">中</el-radio>
            <el-radio :label="3">高</el-radio>
          </el-radio-group>
        </el-form-item>
        
        <el-form-item label="截止时间" prop="deadline">
          <el-date-picker
            v-model="taskForm.deadline"
            type="datetime"
            placeholder="选择截止时间"
            style="width: 100%"
            value-format="YYYY-MM-DDTHH:mm:ss"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm" :loading="submitLoading">
          确定
        </el-button>
      </template>
    </el-dialog>

    <el-dialog
      v-model="reportDialogVisible"
      title="提交巡检上报"
      width="640px"
      destroy-on-close
    >
      <el-form ref="reportFormRef" :model="reportForm" :rules="reportRules" label-width="100px">
        <el-form-item label="任务名称">
          <div class="task-title-text">{{ currentTask?.title || '-' }}</div>
        </el-form-item>
        <el-form-item label="巡检位置" prop="location">
          <el-input v-model="reportForm.location" placeholder="请输入巡检位置" />
        </el-form-item>
        <el-form-item label="上报内容" prop="reportContent">
          <el-input
            v-model="reportForm.reportContent"
            type="textarea"
            :rows="4"
            placeholder="请输入巡检过程、发现问题和处理情况"
          />
        </el-form-item>
        <el-form-item label="检查结果" prop="checkResultText">
          <el-input
            v-model="reportForm.checkResultText"
            type="textarea"
            :rows="4"
            placeholder="请输入检查结果摘要，例如：消防设施完好，二楼配电箱需整改"
          />
        </el-form-item>
        <el-form-item label="现场图片">
          <el-input
            v-model="reportForm.images"
            type="textarea"
            :rows="2"
            placeholder='可填写图片 URL JSON，如 ["https://..."]'
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="reportDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="reportSubmitting" @click="submitTask">
          提交巡检上报
        </el-button>
      </template>
    </el-dialog>

    <el-dialog
      v-model="reviewDialogVisible"
      title="审核上报"
      width="640px"
      destroy-on-close
    >
      <div v-if="currentReport" class="review-summary">
        <div class="review-block">
          <div class="review-label">上报人</div>
          <div>{{ currentReport.reportUserName || currentTask?.executorName || '-' }}</div>
        </div>
        <div class="review-block">
          <div class="review-label">上报时间</div>
          <div>{{ formatDate(currentReport.reportTime) }}</div>
        </div>
        <div class="review-block full">
          <div class="review-label">上报内容</div>
          <div class="review-text">{{ currentReport.reportContent }}</div>
        </div>
        <div class="review-block full" v-if="currentReport.checkResult">
          <div class="review-label">检查结果</div>
          <div class="review-text">{{ formatCheckResult(currentReport.checkResult) }}</div>
        </div>
      </div>
      <el-form ref="reviewFormRef" :model="reviewForm" :rules="reviewRules" label-width="100px">
        <el-form-item label="审核结果" prop="approved">
          <el-radio-group v-model="reviewForm.approved">
            <el-radio :value="true">通过</el-radio>
            <el-radio :value="false">驳回</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="审核意见" prop="reviewComment">
          <el-input
            v-model="reviewForm.reviewComment"
            type="textarea"
            :rows="4"
            placeholder="请输入审核意见"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="reviewDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="reviewSubmitting" @click="submitReview">
          确认审核
        </el-button>
      </template>
    </el-dialog>
    
    <!-- 任务详情对话框 -->
    <el-dialog
      v-model="detailVisible"
      title="任务详情"
      width="600px"
    >
      <el-descriptions :column="2" border v-if="currentTask">
        <el-descriptions-item label="任务ID">{{ currentTask.id }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(currentTask.status)">{{ currentTask.statusName }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="任务标题" :span="2">{{ currentTask.title }}</el-descriptions-item>
        <el-descriptions-item label="任务描述" :span="2">{{ currentTask.description || '-' }}</el-descriptions-item>
        <el-descriptions-item label="检查模板">{{ currentTask.templateName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="优先级">
          <el-tag :type="getPriorityType(currentTask.priority)">{{ currentTask.priorityName }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="执行单位">{{ currentTask.orgName }}</el-descriptions-item>
        <el-descriptions-item label="执行人">{{ currentTask.executorName }}</el-descriptions-item>
        <el-descriptions-item label="派发人">{{ currentTask.assignerName }}</el-descriptions-item>
        <el-descriptions-item label="截止时间">{{ formatDate(currentTask.deadline) }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ formatDate(currentTask.createTime) }}</el-descriptions-item>
        <el-descriptions-item label="完成时间">{{ formatDate(currentTask.completeTime) || '-' }}</el-descriptions-item>
      </el-descriptions>
      <div v-if="taskReports.length" class="report-section">
        <div class="report-section-title">上报记录</div>
        <div v-for="report in taskReports" :key="report.id" class="report-card">
          <div class="report-card-header">
            <span>{{ report.reportUserName || currentTask.executorName || '巡检员' }}</span>
            <el-tag size="small" :type="getReviewStatusType(report.reviewStatus)">
              {{ report.reviewStatusName }}
            </el-tag>
          </div>
          <div class="report-meta">{{ formatDate(report.reportTime) }}{{ report.location ? ` · ${report.location}` : '' }}</div>
          <div class="report-text">{{ report.reportContent }}</div>
          <div class="report-text muted" v-if="report.reviewComment">审核意见：{{ report.reviewComment }}</div>
        </div>
      </div>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { taskApi } from '@/api/task'
import { templateApi } from '@/api/template'
import { organizationApi } from '@/api/organization'
import { userApi } from '@/api/user'
import type {
  Task,
  TaskTemplate,
  Organization,
  User,
  TaskCreateRequest,
  TaskReport,
  TaskReportCreateRequest,
  TaskReviewRequest
} from '@/types'

const userStore = useUserStore()

const loading = ref(false)
const submitLoading = ref(false)
const taskList = ref<Task[]>([])
const templates = ref<TaskTemplate[]>([])
const organizations = ref<Organization[]>([])
const inspectors = ref<User[]>([])

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

const filterForm = reactive({
  status: undefined as number | undefined
})

const dialogVisible = ref(false)
const detailVisible = ref(false)
const reportDialogVisible = ref(false)
const reviewDialogVisible = ref(false)
const isEdit = ref(false)
const currentTask = ref<Task | null>(null)
const currentReport = ref<TaskReport | null>(null)
const taskReports = ref<TaskReport[]>([])

const formRef = ref<FormInstance>()
const reportFormRef = ref<FormInstance>()
const reviewFormRef = ref<FormInstance>()
const taskForm = reactive<TaskCreateRequest>({
  title: '',
  description: '',
  templateId: undefined,
  orgId: 0,
  executorId: 0,
  priority: 2,
  deadline: ''
})
const reportSubmitting = ref(false)
const reviewSubmitting = ref(false)
const reportForm = reactive({
  taskId: 0,
  reportContent: '',
  checkResultText: '',
  images: '',
  location: ''
})
const reviewForm = reactive<TaskReviewRequest>({
  approved: true,
  reviewComment: ''
})

const formRules: FormRules = {
  title: [{ required: true, message: '请输入任务标题', trigger: 'blur' }],
  orgId: [{ required: true, message: '请选择执行单位', trigger: 'change' }],
  executorId: [{ required: true, message: '请选择执行人', trigger: 'change' }],
  deadline: [{ required: true, message: '请选择截止时间', trigger: 'change' }]
}
const reportRules: FormRules = {
  location: [{ required: true, message: '请输入巡检位置', trigger: 'blur' }],
  reportContent: [{ required: true, message: '请输入上报内容', trigger: 'blur' }],
  checkResultText: [{ required: true, message: '请输入检查结果', trigger: 'blur' }]
}
const reviewRules: FormRules = {
  approved: [{ required: true, message: '请选择审核结果', trigger: 'change' }],
  reviewComment: [{ required: true, message: '请输入审核意见', trigger: 'blur' }]
}

const fetchTasks = async () => {
  loading.value = true
  try {
    if (userStore.isInspector) {
      const res = filterForm.status !== undefined
        ? await taskApi.getTasksByStatus(filterForm.status)
        : await taskApi.getMyTasks()
      const inspectorTasks = (res.data || []).filter(task => task.executorId === userStore.user?.id)
      taskList.value = inspectorTasks
      pagination.total = inspectorTasks.length
    } else {
      if (filterForm.status !== undefined) {
        const res = await taskApi.getTasksByStatus(filterForm.status)
        taskList.value = res.data || []
        pagination.total = taskList.value.length
      } else {
        const res = await taskApi.getTasks(pagination.page - 1, pagination.size)
        if (res.data) {
          taskList.value = res.data.content
          pagination.total = res.data.totalElements
        }
      }
    }
  } finally {
    loading.value = false
  }
}

const fetchTemplates = async () => {
  const res = await templateApi.getAll()
  if (res.data) templates.value = res.data
}

const fetchOrganizations = async () => {
  const res = await organizationApi.getSchools()
  if (res.data) organizations.value = res.data
}

const onOrgChange = async (orgId: number) => {
  taskForm.executorId = 0
  inspectors.value = []
  if (orgId) {
    const res = await userApi.getInspectors(orgId)
    if (res.data) inspectors.value = res.data
  }
}

const resetFilter = () => {
  filterForm.status = undefined
  pagination.page = 1
  fetchTasks()
}

const openCreateDialog = () => {
  isEdit.value = false
  Object.assign(taskForm, {
    title: '',
    description: '',
    templateId: undefined,
    orgId: 0,
    executorId: 0,
    priority: 2,
    deadline: ''
  })
  inspectors.value = []
  dialogVisible.value = true
}

const viewTask = (task: Task) => {
  currentTask.value = task
  detailVisible.value = true
  loadTaskReports(task.id)
}

const startTask = async (task: Task) => {
  try {
    await ElMessageBox.confirm('确定开始执行该任务吗？', '提示', {
      type: 'info'
    })
    await taskApi.updateTaskStatus(task.id, 1)
    ElMessage.success('任务已开始执行')
    fetchTasks()
  } catch {
    // 取消操作
  }
}

const openReportDialog = (task: Task) => {
  currentTask.value = task
  Object.assign(reportForm, {
    taskId: task.id,
    reportContent: '',
    checkResultText: '',
    images: '',
    location: ''
  })
  reportDialogVisible.value = true
}

const openReviewDialog = async (task: Task) => {
  currentTask.value = task
  await loadTaskReports(task.id)
  currentReport.value = taskReports.value[0] || null
  Object.assign(reviewForm, {
    approved: true,
    reviewComment: ''
  })
  if (!currentReport.value) {
    ElMessage.warning('当前任务暂无可审核的上报记录')
    return
  }
  reviewDialogVisible.value = true
}

const loadTaskReports = async (taskId: number) => {
  const res = await taskApi.getReportsByTask(taskId)
  taskReports.value = res.data || []
}

const submitTask = async () => {
  if (!reportFormRef.value) return

  await reportFormRef.value.validate(async (valid) => {
    if (!valid) return

    reportSubmitting.value = true
    try {
      const payload: TaskReportCreateRequest = {
        taskId: reportForm.taskId,
        reportContent: reportForm.reportContent,
        location: reportForm.location,
        images: reportForm.images,
        checkResult: JSON.stringify([
          {
            itemName: '巡检摘要',
            remark: reportForm.checkResultText
          }
        ])
      }
      await taskApi.submitReport(payload)
      ElMessage.success('已提交巡检上报')
      reportDialogVisible.value = false
      fetchTasks()
    } finally {
      reportSubmitting.value = false
    }
  })
}

const submitReview = async () => {
  if (!reviewFormRef.value || !currentReport.value) return

  await reviewFormRef.value.validate(async (valid) => {
    if (!valid) return

    reviewSubmitting.value = true
    try {
      await taskApi.reviewReport(currentReport.value!.id, reviewForm)
      ElMessage.success('审核完成')
      reviewDialogVisible.value = false
      fetchTasks()
      if (currentTask.value) {
        await loadTaskReports(currentTask.value.id)
      }
    } finally {
      reviewSubmitting.value = false
    }
  })
}

const deleteTask = async (task: Task) => {
  try {
    await ElMessageBox.confirm('确定删除该任务吗？此操作不可恢复', '警告', {
      type: 'warning'
    })
    await taskApi.deleteTask(task.id)
    ElMessage.success('删除成功')
    fetchTasks()
  } catch {
    // 取消操作
  }
}

const submitForm = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    
    submitLoading.value = true
    try {
      await taskApi.createTask(taskForm)
      ElMessage.success('创建成功')
      dialogVisible.value = false
      fetchTasks()
    } finally {
      submitLoading.value = false
    }
  })
}

const getPriorityType = (priority: number) => {
  const types: Record<number, string> = { 1: 'info', 2: 'warning', 3: 'danger' }
  return types[priority] || 'info'
}

const getStatusType = (status: number) => {
  const types: Record<number, string> = { 0: 'info', 1: 'warning', 2: '', 3: 'success', 4: 'danger' }
  return types[status] || 'info'
}

const getReviewStatusType = (status: number) => {
  const types: Record<number, string> = { 0: 'warning', 1: 'success', 2: 'danger' }
  return types[status] || 'info'
}

const formatDate = (dateStr: string) => {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleString('zh-CN')
}

const formatCheckResult = (value?: string) => {
  if (!value) return '-'
  try {
    const parsed = JSON.parse(value)
    if (Array.isArray(parsed)) {
      return parsed.map(item => item.remark || item.itemName || '').filter(Boolean).join('；')
    }
  } catch {
    return value
  }
  return value
}

const isOverdue = (deadline: string, status: number) => {
  if (status >= 3) return false
  return new Date(deadline) < new Date()
}

onMounted(() => {
  fetchTasks()
  fetchTemplates()
  fetchOrganizations()
})
</script>

<style lang="scss" scoped>
.tasks-page {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  
  .filter-section {
    margin-bottom: 20px;
  }
  
  .pagination-section {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
  }

  .task-title-text {
    color: #303133;
    font-weight: 500;
  }

  .report-section {
    margin-top: 20px;
    border-top: 1px solid #ebeef5;
    padding-top: 16px;
  }

  .report-section-title {
    font-size: 15px;
    font-weight: 600;
    margin-bottom: 12px;
  }

  .report-card {
    padding: 12px;
    border-radius: 8px;
    background: #f8fafc;
    margin-bottom: 10px;
  }

  .report-card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 6px;
  }

  .report-meta {
    color: #909399;
    font-size: 12px;
    margin-bottom: 6px;
  }

  .report-text {
    color: #303133;
    line-height: 1.6;
    white-space: pre-wrap;

    &.muted {
      color: #606266;
      margin-top: 6px;
    }
  }

  .review-summary {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 12px;
    margin-bottom: 16px;
  }

  .review-block {
    padding: 12px;
    background: #f8fafc;
    border-radius: 8px;

    &.full {
      grid-column: 1 / -1;
    }
  }

  .review-label {
    font-size: 12px;
    color: #909399;
    margin-bottom: 6px;
  }
  
  .text-danger {
    color: #f56c6c;
  }
}
</style>
