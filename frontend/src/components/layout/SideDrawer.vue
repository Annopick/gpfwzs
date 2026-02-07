<script setup lang="ts">
defineProps<{
  isOpen: boolean;
  activeMenu: 'chat' | 'profile';
}>();

const emit = defineEmits<{
  close: [];
  select: [menu: 'chat' | 'profile'];
}>();

const menuItems = [
  { key: 'chat' as const, label: '聊天', icon: 'chat' },
  { key: 'profile' as const, label: '我的', icon: 'profile' }
];
</script>

<template>
  <aside class="side-drawer" :class="{ open: isOpen }">
    <div class="drawer-header">
      <h2 class="drawer-title">菜单</h2>
    </div>
    
    <nav class="drawer-nav">
      <button 
        v-for="item in menuItems" 
        :key="item.key"
        class="nav-item"
        :class="{ active: activeMenu === item.key }"
        @click="emit('select', item.key)"
      >
        <span class="nav-icon">
          <svg v-if="item.icon === 'chat'" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M20 2H4c-1.1 0-2 .9-2 2v18l4-4h14c1.1 0 2-.9 2-2V4c0-1.1-.9-2-2-2zm0 14H5.17L4 17.17V4h16v12z" fill="currentColor"/>
          </svg>
          <svg v-else-if="item.icon === 'profile'" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z" fill="currentColor"/>
          </svg>
        </span>
        <span class="nav-label">{{ item.label }}</span>
      </button>
    </nav>
  </aside>
</template>

<style scoped lang="scss">
@use '@/assets/styles/variables' as *;

.side-drawer {
  position: fixed;
  top: 0;
  left: 0;
  width: 280px;
  height: 100vh;
  background-color: $color-background;
  box-shadow: $shadow-medium;
  z-index: 100;
  transform: translateX(-100%);
  transition: transform $transition-base;
  
  &.open {
    transform: translateX(0);
  }
}

.drawer-header {
  height: $nav-height;
  display: flex;
  align-items: center;
  padding: 0 $spacing-lg;
  border-bottom: 1px solid $color-border-light;
}

.drawer-title {
  font-size: $font-size-lg;
  font-weight: 600;
  color: $color-text-primary;
}

.drawer-nav {
  padding: $spacing-md;
}

.nav-item {
  width: 100%;
  display: flex;
  align-items: center;
  gap: $spacing-md;
  padding: $spacing-md;
  background: transparent;
  border-radius: $border-radius-base;
  color: $color-text-secondary;
  transition: all $transition-fast;
  
  &:hover {
    background-color: $color-background-secondary;
    color: $color-text-primary;
  }
  
  &.active {
    background-color: rgba($color-primary, 0.1);
    color: $color-primary;
  }
}

.nav-icon {
  width: 24px;
  height: 24px;
  
  svg {
    width: 100%;
    height: 100%;
  }
}

.nav-label {
  font-size: $font-size-base;
  font-weight: 500;
}
</style>
