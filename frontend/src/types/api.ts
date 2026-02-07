// API响应基础结构
export interface ApiResponse<T = unknown> {
  code: number;
  message: string;
  data?: T;
}

// API错误
export interface ApiError {
  code: number;
  message: string;
  details?: string;
}
