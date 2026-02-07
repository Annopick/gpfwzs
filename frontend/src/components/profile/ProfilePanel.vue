<script setup lang="ts">
import { useAuthStore } from '@/stores/auth';
import { useRouter } from 'vue-router';

const authStore = useAuthStore();
const router = useRouter();

function handleLogout() {
  authStore.clearAuth();
  router.push({ name: 'Login' });
}
</script>

<template>
  <div class="profile-panel">
    <div class="profile-card">
      <div class="avatar">
        <img 
          v-if="authStore.user?.avatar" 
          :src="authStore.user.avatar" 
          :alt="authStore.user?.displayName || '用户头像'"
          class="avatar-img"
        />
        <svg v-else viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z" fill="currentColor"/>
        </svg>
      </div>
      
      <div class="user-info">
        <p class="display-name">{{ authStore.user?.displayName || '用户' }}</p>
        <p class="label">华为账号用户</p>
      </div>
    </div>
    
    <div class="actions">
      <button class="logout-btn" @click="handleLogout">
        <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M17 7l-1.41 1.41L18.17 11H8v2h10.17l-2.58 2.58L17 17l5-5zM4 5h8V3H4c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h8v-2H4V5z" fill="currentColor"/>
        </svg>
        <span>退出登录</span>
      </button>
    </div>
  </div>
</template>

<style scoped lang="scss">
@use '@/assets/styles/variables' as *;

.profile-panel {
  height: 100%;
  padding: $spacing-lg;
  overflow-y: auto;
}

.profile-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: $spacing-xl;
  background-color: $color-background;
  border-radius: $border-radius-large;
  box-shadow: $shadow-light;
}

.avatar {
  width: 80px;
  height: 80px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: $color-background-tertiary;
  border-radius: 50%;
  margin-bottom: $spacing-md;
  color: $color-primary;
  overflow: hidden;
  
  svg {
    width: 48px;
    height: 48px;
  }
  
  .avatar-img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
}

.user-info {
  text-align: center;
}

.display-name {
  font-size: $font-size-xl;
  font-weight: 600;
  color: $color-text-primary;
  margin-bottom: $spacing-xs;
}

.label {
  font-size: $font-size-sm;
  color: $color-text-secondary;
}

.actions {
  margin-top: $spacing-xl;
}

.logout-btn {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: $spacing-sm;
  padding: $spacing-md;
  background-color: $color-background;
  border-radius: $border-radius-base;
  color: $color-danger;
  font-size: $font-size-base;
  transition: background-color $transition-fast;
  
  &:hover {
    background-color: rgba($color-danger, 0.1);
  }
  
  svg {
    width: 20px;
    height: 20px;
  }
}
</style>
