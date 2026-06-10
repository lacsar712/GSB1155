import { get, post, put, del } from './request'
import type { User, ApiResponse, UserCreateRequest } from '@/types'

export const userApi = {
  getAll(): Promise<ApiResponse<User[]>> {
    return get<User[]>('/users')
  },

  getById(id: number): Promise<ApiResponse<User>> {
    return get<User>(`/users/${id}`)
  },

  getByOrg(orgId: number): Promise<ApiResponse<User[]>> {
    return get<User[]>(`/users/org/${orgId}`)
  },

  getInspectors(orgId?: number): Promise<ApiResponse<User[]>> {
    const params = orgId ? { orgId } : {}
    return get<User[]>('/users/inspectors', { params })
  },

  create(data: UserCreateRequest): Promise<ApiResponse<User>> {
    return post<User>('/users', data)
  },

  update(id: number, data: Partial<User>): Promise<ApiResponse<User>> {
    return put<User>(`/users/${id}`, data)
  },

  delete(id: number): Promise<ApiResponse<void>> {
    return del<void>(`/users/${id}`)
  }
}
