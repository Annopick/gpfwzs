<script setup lang="ts">
import { ref } from 'vue';
import TopNavBar from '@/components/layout/TopNavBar.vue';
import SideDrawer from '@/components/layout/SideDrawer.vue';
import ChatPanel from '@/components/chat/ChatPanel.vue';
import ProfilePanel from '@/components/profile/ProfilePanel.vue';

const isDrawerOpen = ref(false);
const activeMenu = ref<'chat' | 'profile'>('chat');

function toggleDrawer() {
  isDrawerOpen.value = !isDrawerOpen.value;
}

function closeDrawer() {
  isDrawerOpen.value = false;
}

function handleMenuSelect(menu: 'chat' | 'profile') {
  activeMenu.value = menu;
  closeDrawer();
}

const pageTitle = ref('智能助手');
</script>

<template>
  <div class="home-page">
    <TopNavBar 
      :title="pageTitle" 
      @toggle-drawer="toggleDrawer" 
    />
    
    <SideDrawer 
      :is-open="isDrawerOpen" 
      :active-menu="activeMenu"
      @close="closeDrawer"
      @select="handleMenuSelect"
    />
    
    <main class="main-content">
      <ChatPanel v-if="activeMenu === 'chat'" />
      <ProfilePanel v-else-if="activeMenu === 'profile'" />
    </main>
    
    <div 
      v-if="isDrawerOpen" 
      class="overlay" 
      @click="closeDrawer"
    ></div>
  </div>
</template>

<style scoped lang="scss">
@use '@/assets/styles/variables' as *;

.home-page {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: $color-background-secondary;
  position: relative;
  overflow: hidden;
}

.main-content {
  flex: 1;
  overflow: hidden;
  margin-top: $nav-height;
}

.overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.3);
  z-index: 90;
}
</style>
