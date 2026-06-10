import { post, get } from './request'
import type { LoginRequest, LoginResponse, User, ApiResponse } from '@/types'

export const authApi = {
  login(data: LoginRequest): Promise<ApiResponse<LoginResponse>> {
    return post<LoginResponse>('/auth/login', data)
  },

  getCurrentUser(): Promise<ApiResponse<User>> {
    return get<User>('/auth/me')
  },

  logout(): Promise<ApiResponse<void>> {
    return post<void>('/auth/logout')
  }
}
