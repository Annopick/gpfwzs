<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { useAuthStore } from '@/stores/auth';

const router = useRouter();
const route = useRoute();
const authStore = useAuthStore();

const status = ref<'loading' | 'success' | 'error'>('loading');
const errorMessage = ref('');

onMounted(async () => {
  const token = route.query.token as string;
  const error = route.query.error as string;
  const needInvite = route.query.need_invite as string;
  const pendingToken = route.query.pending_token as string;

  // Handle need_invite case - redirect to invite code page
  if (needInvite === 'true' && pendingToken) {
    router.push({ 
      name: 'InviteCode', 
      query: { pending_token: pendingToken } 
    });
    return;
  }

  // Handle error case
  if (error) {
    status.value = 'error';
    errorMessage.value = error === 'oauth_failed' 
      ? '登录失败，请稍后重试' 
      : '登录过程出现错误';
    
    setTimeout(() => {
      router.push({ name: 'Login' });
    }, 2000);
    return;
  }

  // Handle success case with token
  if (token) {
    try {
      authStore.setToken(token);
      await authStore.loadUserInfo();
      status.value = 'success';
      
      setTimeout(() => {
        router.push({ name: 'Home' });
      }, 500);
    } catch (e) {
      console.error('Auth failed:', e);
      status.value = 'error';
      errorMessage.value = '认证失败，请重新登录';
      authStore.clearAuth();
      
      setTimeout(() => {
        router.push({ name: 'Login' });
      }, 1500);
    }
  } else {
    status.value = 'error';
    errorMessage.value = '无效的登录回调';
    
    setTimeout(() => {
      router.push({ name: 'Login' });
    }, 1500);
  }
});
</script>

<template>
  <div class="callback-page">
    <div class="callback-container">
      <div v-if="status === 'loading'" class="status-section">
        <div class="spinner"></div>
        <p class="status-text">正在验证登录信息...</p>
      </div>
      
      <div v-else-if="status === 'success'" class="status-section">
        <div class="success-icon">
          <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41L9 16.17z" fill="currentColor"/>
          </svg>
        </div>
        <p class="status-text">登录成功，正在跳转...</p>
      </div>
      
      <div v-else class="status-section">
        <div class="error-icon">
          <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-2h2v2zm0-4h-2V7h2v6z" fill="currentColor"/>
          </svg>
        </div>
        <p class="status-text error">{{ errorMessage }}</p>
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
@use '@/assets/styles/variables' as *;

.callback-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: $color-background;
}

.callback-container {
  text-align: center;
  padding: $spacing-xl;
}

.status-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: $spacing-lg;
}

.spinner {
  width: 48px;
  height: 48px;
  border: 3px solid $color-border;
  border-top-color: $color-primary;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.success-icon {
  width: 48px;
  height: 48px;
  color: $color-success;
  
  svg {
    width: 100%;
    height: 100%;
  }
}

.error-icon {
  width: 48px;
  height: 48px;
  color: $color-danger;
  
  svg {
    width: 100%;
    height: 100%;
  }
}

.status-text {
  font-size: $font-size-base;
  color: $color-text-secondary;
  
  &.error {
    color: $color-danger;
  }
}
</style>
