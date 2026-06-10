import { get } from './request'
import type { DashboardStats, ApiResponse } from '@/types'

export const dashboardApi = {
  getStats(): Promise<ApiResponse<DashboardStats>> {
    return get<DashboardStats>('/dashboard/stats')
  },

  getAllStats(): Promise<ApiResponse<DashboardStats>> {
    return get<DashboardStats>('/dashboard/stats/all')
  }
}
