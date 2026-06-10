import { get, post, put, del } from './request'
import type { Organization, ApiResponse } from '@/types'

export const organizationApi = {
  getAll(): Promise<ApiResponse<Organization[]>> {
    return get<Organization[]>('/organizations')
  },

  getTree(): Promise<ApiResponse<Organization[]>> {
    return get<Organization[]>('/organizations/tree')
  },

  getById(id: number): Promise<ApiResponse<Organization>> {
    return get<Organization>(`/organizations/${id}`)
  },

  getChildren(parentId: number): Promise<ApiResponse<Organization[]>> {
    return get<Organization[]>(`/organizations/children/${parentId}`)
  },

  getSchools(): Promise<ApiResponse<Organization[]>> {
    return get<Organization[]>('/organizations/schools')
  },

  create(data: Partial<Organization>): Promise<ApiResponse<Organization>> {
    return post<Organization>('/organizations', data)
  },

  update(id: number, data: Partial<Organization>): Promise<ApiResponse<Organization>> {
    return put<Organization>(`/organizations/${id}`, data)
  },

  delete(id: number): Promise<ApiResponse<void>> {
    return del<void>(`/organizations/${id}`)
  }
}
