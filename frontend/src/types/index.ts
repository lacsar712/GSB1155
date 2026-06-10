// API 响应类型
export interface ApiResponse<T> {
  code: number
  message: string
  data: T
}

// 用户相关类型
export interface User {
  id: number
  username: string
  realName: string
  phone: string
  email: string
  avatar: string
  orgId: number
  orgName: string
  roleType: number
  roleName: string
  status: number
}

export interface LoginRequest {
  username: string
  passwordHash: string
}

export interface UserCreateRequest {
  username: string
  passwordHash: string
  realName: string
  phone?: string
  email?: string
  orgId: number
  roleType: number
  status?: number
}

export interface LoginResponse {
  token: string
  tokenType: string
  user: User
}

// 组织机构类型
export interface Organization {
  id: number
  name: string
  code: string
  parentId: number
  orgType: number
  sortOrder: number
  status: number
  children?: Organization[]
}

// 任务模板类型
export interface TaskTemplate {
  id: number
  name: string
  description: string
  category: string
  checkItems: string
  createUserId: number
  status: number
  createTime: string
}

export interface CheckItem {
  id: number
  name: string
  required: boolean
  description: string
}

// 任务类型
export interface Task {
  id: number
  title: string
  description: string
  templateId: number
  templateName: string
  orgId: number
  orgName: string
  executorId: number
  executorName: string
  assignerId: number
  assignerName: string
  priority: number
  priorityName: string
  status: number
  statusName: string
  deadline: string
  startTime: string
  completeTime: string
  createTime: string
}

export interface TaskCreateRequest {
  title: string
  description?: string
  templateId?: number
  orgId: number
  executorId: number
  priority?: number
  deadline: string
}

export interface TaskReport {
  id: number
  taskId: number
  taskTitle?: string
  reportContent: string
  checkResult?: string
  images?: string
  location?: string
  reportUserId: number
  reportUserName?: string
  reportTime: string
  reviewStatus: number
  reviewStatusName: string
  reviewUserId?: number
  reviewTime?: string
  reviewComment?: string
}

export interface TaskReportCreateRequest {
  taskId: number
  reportContent: string
  checkResult?: string
  images?: string
  location?: string
}

export interface TaskReviewRequest {
  approved: boolean
  reviewComment?: string
}

// 分页类型
export interface PageResult<T> {
  content: T[]
  totalElements: number
  totalPages: number
  size: number
  number: number
}

// 统计数据类型
export interface DashboardStats {
  totalTasks: number
  pendingTasks: number
  inProgressTasks: number
  completedTasks: number
  overdueCount: number
  pendingReviewCount: number
  totalUsers: number
  totalSchools: number
  totalTemplates: number
}
