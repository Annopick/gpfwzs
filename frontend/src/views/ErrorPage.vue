<script setup lang="ts">
import { useRoute, useRouter } from 'vue-router';

const route = useRoute();
const router = useRouter();

const reason = route.query.reason as string;

interface ErrorInfo {
  title: string;
  message: string;
}

const errorMessages: Record<string, ErrorInfo> = {
  whitelist_failed: {
    title: '访问受限',
    message: '您的账号不在授权白名单中，请联系管理员申请访问权限。'
  },
  default: {
    title: '出错了',
    message: '系统遇到了一些问题，请稍后重试。'
  }
};

const errorInfo: ErrorInfo = errorMessages[reason] || {
  title: '出错了',
  message: '系统遇到了一些问题，请稍后重试。'
};

function goBack() {
  router.push({ name: 'Login' });
}
</script>

<template>
  <div class="error-page">
    <div class="error-container">
      <div class="error-icon">
        <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z" fill="currentColor"/>
        </svg>
      </div>
      
      <h1 class="error-title">{{ errorInfo.title }}</h1>
      <p class="error-message">{{ errorInfo.message }}</p>
      
      <button class="back-btn" @click="goBack">
        返回登录
      </button>
    </div>
  </div>
</template>

<style scoped lang="scss">
@use '@/assets/styles/variables' as *;

.error-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: $color-background;
  padding: $spacing-xl;
}

.error-container {
  text-align: center;
  max-width: 320px;
}

.error-icon {
  width: 80px;
  height: 80px;
  margin: 0 auto $spacing-lg;
  color: $color-warning;
  
  svg {
    width: 100%;
    height: 100%;
  }
}

.error-title {
  font-size: $font-size-xl;
  font-weight: 600;
  color: $color-text-primary;
  margin-bottom: $spacing-sm;
}

.error-message {
  font-size: $font-size-base;
  color: $color-text-secondary;
  line-height: 1.6;
  margin-bottom: $spacing-xl;
}

.back-btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: $spacing-sm $spacing-xl;
  background-color: $color-primary;
  color: #fff;
  border-radius: $border-radius-round;
  font-size: $font-size-base;
  font-weight: 500;
  transition: all $transition-fast;
  
  &:hover {
    background-color: $color-primary-light;
  }
}
</style>
