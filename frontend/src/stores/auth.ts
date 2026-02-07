import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import { storage } from '@/utils/storage';
import { getUserInfo } from '@/api/auth';
import type { UserInfo, JwtPayload } from '@/types/auth';

export const useAuthStore = defineStore('auth', () => {
  // 状态
  const token = ref<string | null>(storage.getToken());
  const user = ref<UserInfo | null>(storage.getUser<UserInfo>());

  // 计算属性
  const isAuthenticated = computed(() => !!token.value && !isTokenExpired());

  // 检查Token是否过期
  function isTokenExpired(): boolean {
    if (!token.value) return true;
    
    try {
      const payload = parseJwt(token.value);
      return payload.exp * 1000 < Date.now();
    } catch {
      return true;
    }
  }

  // 解析JWT
  function parseJwt(jwt: string): JwtPayload {
    const base64Url = jwt.split('.')[1];
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    const jsonPayload = decodeURIComponent(
      atob(base64)
        .split('')
        .map(c => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
        .join('')
    );
    return JSON.parse(jsonPayload);
  }

  // 设置Token
  function setToken(newToken: string) {
    token.value = newToken;
    storage.setToken(newToken);
  }

  // 设置用户信息
  function setUser(newUser: UserInfo) {
    user.value = newUser;
    storage.setUser(newUser);
  }

  // 加载用户信息
  async function loadUserInfo() {
    if (!token.value) return;
    
    try {
      const userInfo = await getUserInfo();
      setUser(userInfo);
    } catch (error) {
      console.error('Failed to load user info:', error);
      clearAuth();
    }
  }

  // 清除认证信息
  function clearAuth() {
    token.value = null;
    user.value = null;
    storage.clearAuth();
  }

  // 初始化时检查Token有效性
  function init() {
    if (token.value && isTokenExpired()) {
      clearAuth();
    }
  }

  // 执行初始化
  init();

  return {
    token,
    user,
    isAuthenticated,
    setToken,
    setUser,
    loadUserInfo,
    clearAuth,
    isTokenExpired
  };
});
