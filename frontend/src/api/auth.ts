import { http } from '@/utils/request';
import type { UserInfo } from '@/types/auth';

/**
 * 从后端获取华为OAuth授权URL
 * OAuth配置（client_id, client_secret等）都由后端管理，不暴露给前端
 */
export async function getHuaweiOAuthUrl(): Promise<string> {
  const response = await http.get<{ code: number; message: string; data: { url: string } }>('/auth/oauth-url');
  return response.data.url;
}

/**
 * 获取当前用户信息
 */
export async function getUserInfo(): Promise<UserInfo> {
  const response = await http.get<{ code: number; message: string; data: UserInfo }>('/users/me');
  return response.data;
}

/**
 * 验证邀请码
 * @param code 邀请码
 * @param pendingToken 待验证的用户信息token
 * @returns JWT token
 */
export async function validateInviteCode(code: string, pendingToken: string): Promise<string> {
  const response = await http.post<{ code: number; message: string; data: { token: string } }>(
    '/invite-codes/validate',
    { code, pendingToken }
  );
  return response.data.token;
}
