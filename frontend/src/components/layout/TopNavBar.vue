<script setup lang="ts">
defineProps<{
  title: string;
}>();

const emit = defineEmits<{
  toggleDrawer: [];
}>();
</script>

<template>
  <header class="top-nav">
    <button class="menu-btn" @click="emit('toggleDrawer')">
      <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
        <path d="M3 18h18v-2H3v2zm0-5h18v-2H3v2zm0-7v2h18V6H3z" fill="currentColor"/>
      </svg>
    </button>
    
    <h1 class="nav-title">{{ title }}</h1>
    
    <div class="nav-spacer"></div>
  </header>
</template>

<style scoped lang="scss">
@use '@/assets/styles/variables' as *;

.top-nav {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: $nav-height;
  display: flex;
  align-items: center;
  padding: 0 $spacing-md;
  background-color: $color-background;
  border-bottom: 1px solid $color-border-light;
  z-index: 80;
}

.menu-btn {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: transparent;
  border-radius: $border-radius-base;
  color: $color-text-primary;
  transition: background-color $transition-fast;
  
  &:hover {
    background-color: $color-background-secondary;
  }
  
  svg {
    width: 24px;
    height: 24px;
  }
}

.nav-title {
  flex: 1;
  text-align: center;
  font-size: $font-size-lg;
  font-weight: 600;
  color: $color-text-primary;
}

.nav-spacer {
  width: 40px;
}
</style>
