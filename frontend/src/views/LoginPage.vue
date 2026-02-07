<script setup lang="ts">
import { ref } from 'vue';
import { getHuaweiOAuthUrl } from '@/api/auth';

const isLoading = ref(false);

async function handleLogin() {
  if (isLoading.value) return;
  
  isLoading.value = true;
  try {
    const url = await getHuaweiOAuthUrl();
    window.location.href = url;
  } catch (error) {
    console.error('Failed to get OAuth URL:', error);
    isLoading.value = false;
  }
}
</script>

<template>
  <div class="login-page">
    <div class="login-container">
      <div class="logo-section">
        <div class="logo">
          <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-1 17.93c-3.95-.49-7-3.85-7-7.93 0-.62.08-1.21.21-1.79L9 15v1c0 1.1.9 2 2 2v1.93zm6.9-2.54c-.26-.81-1-1.39-1.9-1.39h-1v-3c0-.55-.45-1-1-1H8v-2h2c.55 0 1-.45 1-1V7h2c1.1 0 2-.9 2-2v-.41c2.93 1.19 5 4.06 5 7.41 0 2.08-.8 3.97-2.1 5.39z" fill="currentColor"/>
          </svg>
        </div>
        <h1 class="title">智能助手</h1>
        <p class="subtitle">您的个人AI对话伙伴</p>
      </div>
      
      <div class="login-section">
        <button class="login-btn" @click="handleLogin">
          <svg class="huawei-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M12 2L2 7l10 5 10-5-10-5zM2 17l10 5 10-5M2 12l10 5 10-5" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
          <span>华为用户登录</span>
        </button>
        
        <p class="login-tip">使用华为账号快速登录</p>
      </div>
    </div>
    
    <footer class="footer">
      <p>仅限授权用户使用</p>
    </footer>
  </div>
</template>

<style scoped lang="scss">
@use '@/assets/styles/variables' as *;

.login-page {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: linear-gradient(180deg, $color-background 0%, $color-background-secondary 100%);
}

.login-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: $spacing-xl;
}

.logo-section {
  text-align: center;
  margin-bottom: $spacing-xl * 2;
}

.logo {
  width: 80px;
  height: 80px;
  margin: 0 auto $spacing-lg;
  color: $color-primary;
  
  svg {
    width: 100%;
    height: 100%;
  }
}

.title {
  font-size: 28px;
  font-weight: 600;
  color: $color-text-primary;
  margin-bottom: $spacing-sm;
}

.subtitle {
  font-size: $font-size-base;
  color: $color-text-secondary;
}

.login-section {
  width: 100%;
  max-width: 320px;
}

.login-btn {
  width: 100%;
  height: 52px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: $spacing-sm;
  background-color: #CF0A2C;
  color: #fff;
  border-radius: $border-radius-round;
  font-size: $font-size-base;
  font-weight: 500;
  transition: all $transition-fast;
  
  &:hover {
    background-color: #B00926;
    transform: translateY(-1px);
    box-shadow: 0 4px 12px rgba(207, 10, 44, 0.3);
  }
  
  &:active {
    transform: translateY(0);
  }
}

.huawei-icon {
  width: 20px;
  height: 20px;
}

.login-tip {
  text-align: center;
  margin-top: $spacing-md;
  font-size: $font-size-sm;
  color: $color-text-secondary;
}

.footer {
  padding: $spacing-lg;
  text-align: center;
  
  p {
    font-size: $font-size-xs;
    color: $color-text-placeholder;
  }
}
</style>
