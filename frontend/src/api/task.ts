import { get, post, put, del, patch } from './request'
import type {
  Task,
  TaskCreateRequest,
  TaskReport,
  TaskReportCreateRequest,
  TaskReviewRequest,
  PageResult,
  ApiResponse
} from '@/types'

export const taskApi = {
  getTasks(page = 0, size = 10): Promise<ApiResponse<PageResult<Task>>> {
    return get<PageResult<Task>>('/tasks', { params: { page, size } })
  },

  getTaskById(id: number): Promise<ApiResponse<Task>> {
    return get<Task>(`/tasks/${id}`)
  },

  getTasksByOrg(orgId: number): Promise<ApiResponse<Task[]>> {
    return get<Task[]>(`/tasks/org/${orgId}`)
  },

  getMyTasks(): Promise<ApiResponse<Task[]>> {
    return get<Task[]>('/tasks/my')
  },

  getMyPendingTasks(): Promise<ApiResponse<Task[]>> {
    return get<Task[]>('/tasks/my/pending')
  },

  getTasksByStatus(status: number): Promise<ApiResponse<Task[]>> {
    return get<Task[]>(`/tasks/status/${status}`)
  },

  getReportsByTask(taskId: number): Promise<ApiResponse<TaskReport[]>> {
    return get<TaskReport[]>(`/task-reports/task/${taskId}`)
  },

  getPendingReports(): Promise<ApiResponse<TaskReport[]>> {
    return get<TaskReport[]>('/task-reports/pending-review')
  },

  submitReport(data: TaskReportCreateRequest): Promise<ApiResponse<TaskReport>> {
    return post<TaskReport>('/task-reports', data)
  },

  reviewReport(id: number, data: TaskReviewRequest): Promise<ApiResponse<TaskReport>> {
    return post<TaskReport>(`/task-reports/${id}/review`, data)
  },

  createTask(data: TaskCreateRequest): Promise<ApiResponse<Task>> {
    return post<Task>('/tasks', data)
  },

  updateTask(id: number, data: TaskCreateRequest): Promise<ApiResponse<Task>> {
    return put<Task>(`/tasks/${id}`, data)
  },

  updateTaskStatus(id: number, status: number): Promise<ApiResponse<Task>> {
    return patch<Task>(`/tasks/${id}/status`, { status })
  },

  deleteTask(id: number): Promise<ApiResponse<void>> {
    return del<void>(`/tasks/${id}`)
  }
}
