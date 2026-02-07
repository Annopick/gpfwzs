// 用户信息
export interface UserInfo {
  id: number;
  openId: string;
  displayName?: string;
  avatar?: string;
}

// 登录响应
export interface LoginResponse {
  token: string;
  expiresIn: number;
}

// JWT载荷
export interface JwtPayload {
  sub: string;
  openId: string;
  displayName?: string;
  iat: number;
  exp: number;
}
