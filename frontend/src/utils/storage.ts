const TOKEN_KEY = 'gpfwzs_token';
const USER_KEY = 'gpfwzs_user';

export const storage = {
  // Token操作
  getToken(): string | null {
    return localStorage.getItem(TOKEN_KEY);
  },

  setToken(token: string): void {
    localStorage.setItem(TOKEN_KEY, token);
  },

  removeToken(): void {
    localStorage.removeItem(TOKEN_KEY);
  },

  // 用户信息操作
  getUser<T>(): T | null {
    const data = localStorage.getItem(USER_KEY);
    if (data) {
      try {
        return JSON.parse(data) as T;
      } catch {
        return null;
      }
    }
    return null;
  },

  setUser<T>(user: T): void {
    localStorage.setItem(USER_KEY, JSON.stringify(user));
  },

  removeUser(): void {
    localStorage.removeItem(USER_KEY);
  },

  // 清除所有认证信息
  clearAuth(): void {
    this.removeToken();
    this.removeUser();
  }
};
