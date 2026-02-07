<script setup lang="ts">
import { ref, watch, nextTick, computed } from 'vue';
import { marked } from 'marked';
import type { ChatMessage } from '@/types/chat';

// 配置 marked
marked.setOptions({
  breaks: false,  // 不自动转换单换行为 <br>
  gfm: true
});

const props = defineProps<{
  messages: ChatMessage[];
  isStreaming: boolean;
}>();

const listRef = ref<HTMLDivElement | null>(null);

// 清理并渲染 markdown 内容
function renderMarkdown(content: string): string {
  if (!content) return '';
  
  // 预处理：清理多余空行和 <br/> 标签
  let cleaned = content
    .replace(/<br\s*\/?>/gi, '\n')      // 将 <br/> 转为换行
    .replace(/\n{3,}/g, '\n\n')          // 多于2个换行压缩为2个
    .replace(/^\s+|\s+$/g, '')           // 去除首尾空白
    .replace(/\n\s*\n\s*\n/g, '\n\n');   // 再次清理多余空行
  
  // 渲染 markdown
  let html = marked.parse(cleaned) as string;
  
  // 后处理：清理渲染后的多余空段落
  html = html
    .replace(/<p>\s*<\/p>/g, '')         // 移除空段落
    .replace(/(<br\s*\/?>\s*){2,}/g, '<br>'); // 多个 <br> 合并为一个
  
  return html;
}

watch(
  () => props.messages.length,
  async () => {
    await nextTick();
    scrollToBottom();
  }
);

watch(
  () => props.messages[props.messages.length - 1]?.content,
  async () => {
    await nextTick();
    scrollToBottom();
  }
);

function scrollToBottom() {
  if (listRef.value) {
    listRef.value.scrollTop = listRef.value.scrollHeight;
  }
}
</script>

<template>
  <div ref="listRef" class="message-list">
    <div v-if="messages.length === 0" class="empty-state">
      <div class="empty-icon">
        <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M20 2H4c-1.1 0-2 .9-2 2v18l4-4h14c1.1 0 2-.9 2-2V4c0-1.1-.9-2-2-2zm0 14H5.17L4 17.17V4h16v12z" fill="currentColor"/>
        </svg>
      </div>
      <p class="empty-text">开始新的对话吧</p>
      <p class="empty-hint">输入您想问的问题，AI助手将为您解答</p>
    </div>
    
    <div 
      v-for="message in messages" 
      :key="message.id"
      class="message-item"
      :class="message.role"
    >
      <div class="message-avatar">
        <svg v-if="message.role === 'user'" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z" fill="currentColor"/>
        </svg>
        <svg v-else viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-1 17.93c-3.95-.49-7-3.85-7-7.93 0-.62.08-1.21.21-1.79L9 15v1c0 1.1.9 2 2 2v1.93zm6.9-2.54c-.26-.81-1-1.39-1.9-1.39h-1v-3c0-.55-.45-1-1-1H8v-2h2c.55 0 1-.45 1-1V7h2c1.1 0 2-.9 2-2v-.41c2.93 1.19 5 4.06 5 7.41 0 2.08-.8 3.97-2.1 5.39z" fill="currentColor"/>
        </svg>
      </div>
      <div class="message-content">
        <div 
          v-if="message.role === 'assistant'" 
          class="message-text markdown-body" 
          v-html="renderMarkdown(message.content)"
        ></div>
        <p v-else class="message-text">{{ message.content }}</p>
        <span v-if="isStreaming && message.role === 'assistant' && message === messages[messages.length - 1]" class="typing-cursor"></span>
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
@use '@/assets/styles/variables' as *;

.message-list {
  flex: 1;
  overflow-y: auto;
  padding: $spacing-md;
  padding-bottom: 100px;
}

.empty-state {
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
  padding: $spacing-xl;
}

.empty-icon {
  width: 64px;
  height: 64px;
  margin-bottom: $spacing-md;
  color: $color-border;
  
  svg {
    width: 100%;
    height: 100%;
  }
}

.empty-text {
  font-size: $font-size-lg;
  color: $color-text-secondary;
  margin-bottom: $spacing-sm;
}

.empty-hint {
  font-size: $font-size-sm;
  color: $color-text-placeholder;
}

.message-item {
  display: flex;
  gap: $spacing-sm;
  margin-bottom: $spacing-md;
  
  &.user {
    flex-direction: row-reverse;
    
    .message-content {
      background-color: $color-primary;
      color: #fff;
    }
    
    .message-avatar {
      background-color: $color-primary-light;
      color: #fff;
    }
  }
  
  &.assistant {
    .message-content {
      background-color: $color-background;
    }
    
    .message-avatar {
      background-color: $color-background-tertiary;
      color: $color-primary;
    }
  }
}

.message-avatar {
  width: 36px;
  height: 36px;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  
  svg {
    width: 20px;
    height: 20px;
  }
}

.message-content {
  max-width: 75%;
  padding: $spacing-sm $spacing-md;
  border-radius: $border-radius-large;
  box-shadow: $shadow-light;
}

.message-text {
  font-size: $font-size-base;
  line-height: 1.6;
  white-space: pre-wrap;
  word-break: break-word;
}

// Markdown 样式
.markdown-body {
  font-size: $font-size-base;
  line-height: 1.6;
  word-break: break-word;
  
  p {
    margin: 0 0 0.5em 0;
    &:last-child {
      margin-bottom: 0;
    }
  }
  
  h1, h2, h3, h4, h5, h6 {
    margin: 0.5em 0 0.3em 0;
    font-weight: 600;
  }
  
  h1 { font-size: 1.4em; }
  h2 { font-size: 1.3em; }
  h3 { font-size: 1.2em; }
  h4 { font-size: 1.1em; }
  
  ul, ol {
    margin: 0.3em 0;
    padding-left: 1.5em;
  }
  
  li {
    margin: 0.2em 0;
  }
  
  code {
    background-color: rgba(0, 0, 0, 0.06);
    padding: 0.15em 0.4em;
    border-radius: 4px;
    font-family: 'Monaco', 'Menlo', monospace;
    font-size: 0.9em;
  }
  
  pre {
    background-color: rgba(0, 0, 0, 0.06);
    padding: 0.8em;
    border-radius: 6px;
    overflow-x: auto;
    margin: 0.5em 0;
    
    code {
      background: none;
      padding: 0;
      font-size: 0.85em;
    }
  }
  
  blockquote {
    margin: 0.5em 0;
    padding: 0.3em 0.8em;
    border-left: 3px solid $color-primary;
    background-color: rgba(0, 0, 0, 0.03);
  }
  
  table {
    border-collapse: collapse;
    margin: 0.5em 0;
    width: 100%;
    
    th, td {
      border: 1px solid $color-border;
      padding: 0.4em 0.6em;
      text-align: left;
    }
    
    th {
      background-color: rgba(0, 0, 0, 0.04);
      font-weight: 600;
    }
  }
  
  a {
    color: $color-primary;
    text-decoration: none;
    &:hover {
      text-decoration: underline;
    }
  }
  
  strong {
    font-weight: 600;
  }
  
  em {
    font-style: italic;
  }
}

.typing-cursor {
  display: inline-block;
  width: 2px;
  height: 1em;
  background-color: currentColor;
  margin-left: 2px;
  animation: blink 1s infinite;
}

@keyframes blink {
  0%, 50% { opacity: 1; }
  51%, 100% { opacity: 0; }
}
</style>
