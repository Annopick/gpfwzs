<script setup lang="ts">
import MessageList from './MessageList.vue';
import ChatInput from './ChatInput.vue';
import { useChatStore } from '@/stores/chat';

const chatStore = useChatStore();

function handleSend(content: string) {
  chatStore.sendMessage(content);
}
</script>

<template>
  <div class="chat-panel">
    <MessageList 
      :messages="chatStore.messages" 
      :is-streaming="chatStore.isStreaming"
    />
    <ChatInput 
      :disabled="chatStore.isStreaming"
      @send="handleSend" 
    />
  </div>
</template>

<style scoped lang="scss">
.chat-panel {
  height: 100%;
  display: flex;
  flex-direction: column;
  position: relative;
}
</style>
