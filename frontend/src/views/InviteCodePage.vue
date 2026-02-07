<script setup lang="ts">
import { ref } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { useAuthStore } from '@/stores/auth';
import { validateInviteCode } from '@/api/auth';

const router = useRouter();
const route = useRoute();
const authStore = useAuthStore();

const inviteCode = ref('');
const isLoading = ref(false);
const errorMessage = ref('');

const pendingToken = route.query.pending_token as string;

if (!pendingToken) {
  router.push({ name: 'Login' });
}

async function handleSubmit() {
  if (isLoading.value || !inviteCode.value.trim()) return;
  
  errorMessage.value = '';
  isLoading.value = true;
  
  try {
    const token = await validateInviteCode(inviteCode.value.trim(), pendingToken);
    authStore.setToken(token);
    await authStore.loadUserInfo();
    router.push({ name: 'Home' });
  } catch (error: any) {
    console.error('Invite code validation failed:', error);
    errorMessage.value = error.response?.data?.message || '邀请码无效或已使用';
    isLoading.value = false;
  }
}

function handleBack() {
  router.push({ name: 'Login' });
}
</script>

<template>
  <div class="invite-page">
    <div class="invite-container">
      <div class="header-section">
        <div class="icon">
          <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M18 8h-1V6c0-2.76-2.24-5-5-5S7 3.24 7 6v2H6c-1.1 0-2 .9-2 2v10c0 1.1.9 2 2 2h12c1.1 0 2-.9 2-2V10c0-1.1-.9-2-2-2zm-6 9c-1.1 0-2-.9-2-2s.9-2 2-2 2 .9 2 2-.9 2-2 2zm3.1-9H8.9V6c0-1.71 1.39-3.1 3.1-3.1 1.71 0 3.1 1.39 3.1 3.1v2z" fill="currentColor"/>
          </svg>
        </div>
        <h1 class="title">需要邀请码</h1>
        <p class="subtitle">您的账号尚未加入白名单，请输入邀请码继续</p>
      </div>
      
      <div class="form-section">
        <div class="input-group">
          <input
            v-model="inviteCode"
            type="text"
            placeholder="请输入邀请码"
            class="invite-input"
            :disabled="isLoading"
            @keyup.enter="handleSubmit"
          />
        </div>
        
        <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>
        
        <button 
          class="submit-btn" 
          :disabled="isLoading || !inviteCode.trim()"
          @click="handleSubmit"
        >
          <span v-if="isLoading" class="spinner"></span>
          <span v-else>验证邀请码</span>
        </button>
        
        <button class="back-btn" @click="handleBack" :disabled="isLoading">
          返回登录
        </button>
      </div>
    </div>
    
    <footer class="footer">
      <p>如需邀请码，请联系管理员</p>
    </footer>
  </div>
</template>

<style scoped lang="scss">
@use '@/assets/styles/variables' as *;

.invite-page {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: linear-gradient(180deg, $color-background 0%, $color-background-secondary 100%);
}

.invite-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: $spacing-xl;
}

.header-section {
  text-align: center;
  margin-bottom: $spacing-xl * 2;
}

.icon {
  width: 64px;
  height: 64px;
  margin: 0 auto $spacing-lg;
  color: $color-primary;
  
  svg {
    width: 100%;
    height: 100%;
  }
}

.title {
  font-size: 24px;
  font-weight: 600;
  color: $color-text-primary;
  margin-bottom: $spacing-sm;
}

.subtitle {
  font-size: $font-size-sm;
  color: $color-text-secondary;
  max-width: 280px;
  line-height: 1.5;
}

.form-section {
  width: 100%;
  max-width: 320px;
}

.input-group {
  margin-bottom: $spacing-md;
}

.invite-input {
  width: 100%;
  height: 52px;
  padding: 0 $spacing-lg;
  border: 1px solid $color-border;
  border-radius: $border-radius-base;
  font-size: $font-size-base;
  text-align: center;
  text-transform: uppercase;
  letter-spacing: 2px;
  transition: all $transition-fast;
  
  &:focus {
    border-color: $color-primary;
    box-shadow: 0 0 0 2px rgba($color-primary, 0.1);
  }
  
  &:disabled {
    background-color: $color-background-secondary;
    cursor: not-allowed;
  }
  
  &::placeholder {
    text-transform: none;
    letter-spacing: normal;
  }
}

.error-message {
  text-align: center;
  color: $color-danger;
  font-size: $font-size-sm;
  margin-bottom: $spacing-md;
}

.submit-btn {
  width: 100%;
  height: 52px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: $color-primary;
  color: #fff;
  border-radius: $border-radius-round;
  font-size: $font-size-base;
  font-weight: 500;
  transition: all $transition-fast;
  margin-bottom: $spacing-md;
  
  &:hover:not(:disabled) {
    background-color: darken($color-primary, 10%);
    transform: translateY(-1px);
    box-shadow: 0 4px 12px rgba($color-primary, 0.3);
  }
  
  &:active:not(:disabled) {
    transform: translateY(0);
  }
  
  &:disabled {
    background-color: $color-border;
    cursor: not-allowed;
  }
}

.back-btn {
  width: 100%;
  height: 44px;
  background-color: transparent;
  color: $color-text-secondary;
  border: 1px solid $color-border;
  border-radius: $border-radius-round;
  font-size: $font-size-sm;
  transition: all $transition-fast;
  
  &:hover:not(:disabled) {
    background-color: $color-background-secondary;
    color: $color-text-primary;
  }
  
  &:disabled {
    opacity: 0.5;
    cursor: not-allowed;
  }
}

.spinner {
  width: 20px;
  height: 20px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top-color: #fff;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
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
