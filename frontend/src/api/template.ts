import { get, post, put, del } from './request'
import type { TaskTemplate, ApiResponse } from '@/types'

export const templateApi = {
  getAll(): Promise<ApiResponse<TaskTemplate[]>> {
    return get<TaskTemplate[]>('/templates')
  },

  getById(id: number): Promise<ApiResponse<TaskTemplate>> {
    return get<TaskTemplate>(`/templates/${id}`)
  },

  getCategories(): Promise<ApiResponse<string[]>> {
    return get<string[]>('/templates/categories')
  },

  getByCategory(category: string): Promise<ApiResponse<TaskTemplate[]>> {
    return get<TaskTemplate[]>(`/templates/category/${category}`)
  },

  create(data: Partial<TaskTemplate>): Promise<ApiResponse<TaskTemplate>> {
    return post<TaskTemplate>('/templates', data)
  },

  update(id: number, data: Partial<TaskTemplate>): Promise<ApiResponse<TaskTemplate>> {
    return put<TaskTemplate>(`/templates/${id}`, data)
  },

  delete(id: number): Promise<ApiResponse<void>> {
    return del<void>(`/templates/${id}`)
  }
}
