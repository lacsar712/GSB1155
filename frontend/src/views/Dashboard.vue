<template>
  <div class="dashboard">
    <!-- 欢迎信息 -->
    <div class="welcome-section">
      <div class="welcome-content">
        <h2>👋 欢迎回来，{{ userStore.user?.realName }}</h2>
        <p>{{ greeting }}，今天是 {{ currentDate }}，{{ userStore.user?.roleName }}</p>
      </div>
    </div>
    
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :xs="12" :sm="6" v-for="stat in statsCards" :key="stat.key">
        <div :class="['stat-card', stat.colorClass]">
          <div class="stat-value">{{ stats[stat.key as keyof typeof stats] || 0 }}</div>
          <div class="stat-label">{{ stat.label }}</div>
          <el-icon class="stat-icon"><component :is="stat.icon" /></el-icon>
        </div>
      </el-col>
    </el-row>
    
    <!-- 快捷操作 -->
    <el-row :gutter="20" class="action-row">
      <el-col :xs="24" :md="12">
        <el-card class="action-card">
          <template #header>
            <div class="card-header">
              <span><el-icon><List /></el-icon> 快捷操作</span>
            </div>
          </template>
          <div class="quick-actions">
            <el-button type="primary" @click="goToTasks">
              <el-icon><Plus /></el-icon>
              创建任务
            </el-button>
            <el-button @click="goToTemplates">
              <el-icon><Document /></el-icon>
              模板管理
            </el-button>
            <el-button @click="refreshStats">
              <el-icon><Refresh /></el-icon>
              刷新数据
            </el-button>
          </div>
        </el-card>
      </el-col>
      
      <el-col :xs="24" :md="12">
        <el-card class="action-card">
          <template #header>
            <div class="card-header">
              <span><el-icon><Warning /></el-icon> 待办提醒</span>
            </div>
          </template>
          <div class="reminders">
            <el-alert
              v-if="stats.pendingTasks > 0"
              :title="`有 ${stats.pendingTasks} 个任务待执行`"
              type="warning"
              show-icon
              :closable="false"
            />
            <el-alert
              v-if="stats.overdueCount > 0"
              :title="`有 ${stats.overdueCount} 个任务已逾期`"
              type="error"
              show-icon
              :closable="false"
            />
            <el-alert
              v-if="stats.pendingReviewCount > 0"
              :title="`有 ${stats.pendingReviewCount} 个上报待审核`"
              type="info"
              show-icon
              :closable="false"
            />
            <el-empty 
              v-if="!stats.pendingTasks && !stats.overdueCount && !stats.pendingReviewCount"
              description="暂无待办事项"
              :image-size="80"
            />
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 最近任务 -->
    <el-card class="recent-tasks">
      <template #header>
        <div class="card-header">
          <span><el-icon><Clock /></el-icon> 最近任务</span>
          <el-button type="primary" link @click="goToTasks">查看全部</el-button>
        </div>
      </template>
      
      <el-table 
        :data="recentTasks" 
        v-loading="loadingTasks"
        stripe
        style="width: 100%"
      >
        <el-table-column prop="title" label="任务名称" min-width="200" show-overflow-tooltip />
        <el-table-column prop="orgName" label="执行单位" width="150" />
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
      </el-table>
      
      <el-empty v-if="!loadingTasks && recentTasks.length === 0" description="暂无任务数据" />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { dashboardApi } from '@/api/dashboard'
import { taskApi } from '@/api/task'
import type { DashboardStats, Task } from '@/types'

const router = useRouter()
const userStore = useUserStore()

const stats = ref<DashboardStats>({
  totalTasks: 0,
  pendingTasks: 0,
  inProgressTasks: 0,
  completedTasks: 0,
  overdueCount: 0,
  pendingReviewCount: 0,
  totalUsers: 0,
  totalSchools: 0,
  totalTemplates: 0
})

const recentTasks = ref<Task[]>([])
const loadingStats = ref(false)
const loadingTasks = ref(false)

const statsCards = [
  { key: 'totalTasks', label: '总任务数', icon: 'List', colorClass: 'blue' },
  { key: 'pendingTasks', label: '待执行', icon: 'Clock', colorClass: 'orange' },
  { key: 'inProgressTasks', label: '执行中', icon: 'Loading', colorClass: 'cyan' },
  { key: 'completedTasks', label: '已完成', icon: 'CircleCheck', colorClass: 'green' }
]

const greeting = computed(() => {
  const hour = new Date().getHours()
  if (hour < 6) return '凌晨好'
  if (hour < 9) return '早上好'
  if (hour < 12) return '上午好'
  if (hour < 14) return '中午好'
  if (hour < 17) return '下午好'
  if (hour < 19) return '傍晚好'
  return '晚上好'
})

const currentDate = computed(() => {
  const date = new Date()
  const weekDays = ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六']
  return `${date.getFullYear()}年${date.getMonth() + 1}月${date.getDate()}日 ${weekDays[date.getDay()]}`
})

const fetchStats = async () => {
  loadingStats.value = true
  try {
    const res = await dashboardApi.getStats()
    if (res.data) {
      stats.value = res.data
    }
  } finally {
    loadingStats.value = false
  }
}

const fetchRecentTasks = async () => {
  loadingTasks.value = true
  try {
    const res = await taskApi.getTasks(0, 5)
    if (res.data) {
      recentTasks.value = res.data.content
    }
  } finally {
    loadingTasks.value = false
  }
}

const refreshStats = () => {
  fetchStats()
  fetchRecentTasks()
}

const goToTasks = () => router.push('/tasks')
const goToTemplates = () => router.push('/templates')

const getPriorityType = (priority: number) => {
  const types: Record<number, string> = { 1: 'info', 2: 'warning', 3: 'danger' }
  return types[priority] || 'info'
}

const getStatusType = (status: number) => {
  const types: Record<number, string> = { 0: 'info', 1: 'warning', 2: '', 3: 'success', 4: 'danger' }
  return types[status] || 'info'
}

const formatDate = (dateStr: string) => {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const isOverdue = (deadline: string, status: number) => {
  if (status >= 3) return false
  return new Date(deadline) < new Date()
}

onMounted(() => {
  fetchStats()
  fetchRecentTasks()
})
</script>

<style lang="scss" scoped>
.dashboard {
  .welcome-section {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    border-radius: 12px;
    padding: 24px 32px;
    margin-bottom: 20px;
    color: white;
    
    .welcome-content {
      h2 {
        margin: 0 0 8px 0;
        font-size: 24px;
      }
      
      p {
        margin: 0;
        opacity: 0.9;
        font-size: 14px;
      }
    }
  }
  
  .stats-row {
    margin-bottom: 20px;
    
    .el-col {
      margin-bottom: 12px;
    }
  }
  
  .action-row {
    margin-bottom: 20px;
    
    .el-col {
      margin-bottom: 12px;
    }
    
    .action-card {
      height: 100%;
      
      .card-header {
        display: flex;
        align-items: center;
        gap: 8px;
        font-weight: 500;
      }
      
      .quick-actions {
        display: flex;
        flex-wrap: wrap;
        gap: 12px;
      }
      
      .reminders {
        display: flex;
        flex-direction: column;
        gap: 12px;
      }
    }
  }
  
  .recent-tasks {
    .card-header {
      display: flex;
      align-items: center;
      justify-content: space-between;
      
      span {
        display: flex;
        align-items: center;
        gap: 8px;
        font-weight: 500;
      }
    }
    
    .text-danger {
      color: #f56c6c;
    }
  }
}
</style>
