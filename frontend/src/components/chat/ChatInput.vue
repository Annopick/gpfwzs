<script setup lang="ts">
import { ref } from 'vue';

defineProps<{
  disabled?: boolean;
}>();

const emit = defineEmits<{
  send: [content: string];
}>();

const inputText = ref('');

function handleSend() {
  const content = inputText.value.trim();
  if (content) {
    emit('send', content);
    inputText.value = '';
  }
}

function handleKeydown(event: KeyboardEvent) {
  if (event.key === 'Enter' && !event.shiftKey) {
    event.preventDefault();
    handleSend();
  }
}
</script>

<template>
  <div class="chat-input-wrapper">
    <div class="chat-input-container">
      <textarea
        v-model="inputText"
        class="chat-input"
        placeholder="输入您的问题..."
        rows="1"
        :disabled="disabled"
        @keydown="handleKeydown"
      ></textarea>
      <button 
        class="send-btn" 
        :disabled="disabled || !inputText.trim()"
        @click="handleSend"
      >
        <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M2.01 21L23 12 2.01 3 2 10l15 2-15 2z" fill="currentColor"/>
        </svg>
      </button>
    </div>
  </div>
</template>

<style scoped lang="scss">
@use '@/assets/styles/variables' as *;

.chat-input-wrapper {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: $spacing-md;
  background: linear-gradient(transparent, $color-background-secondary 30%);
}

.chat-input-container {
  display: flex;
  align-items: flex-end;
  gap: $spacing-sm;
  padding: $spacing-sm;
  background-color: $color-background;
  border-radius: $border-radius-round;
  box-shadow: $shadow-medium;
}

.chat-input {
  flex: 1;
  min-height: 40px;
  max-height: 120px;
  padding: $spacing-sm $spacing-md;
  border: none;
  outline: none;
  background: transparent;
  font-size: $font-size-base;
  color: $color-text-primary;
  resize: none;
  line-height: 1.5;
  
  &::placeholder {
    color: $color-text-placeholder;
  }
  
  &:disabled {
    opacity: 0.6;
  }
}

.send-btn {
  width: 40px;
  height: 40px;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: $color-primary;
  color: #fff;
  border-radius: 50%;
  transition: all $transition-fast;
  
  &:hover:not(:disabled) {
    background-color: $color-primary-light;
    transform: scale(1.05);
  }
  
  &:disabled {
    background-color: $color-border;
    cursor: not-allowed;
  }
  
  svg {
    width: 20px;
    height: 20px;
  }
}
</style>
