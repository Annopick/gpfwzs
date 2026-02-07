import { defineStore } from 'pinia';
import { ref } from 'vue';
import { sendSSERequest, generateId } from '@/utils/sse';
import { getChatMessagesUrl, saveConversation } from '@/api/chat';
import type { ChatMessage } from '@/types/chat';

export const useChatStore = defineStore('chat', () => {
  // 状态
  const conversationId = ref<string | null>(null);
  const messages = ref<ChatMessage[]>([]);
  const isStreaming = ref(false);
  const currentAssistantMessage = ref<ChatMessage | null>(null);

  // 发送消息
  async function sendMessage(content: string) {
    if (!content.trim() || isStreaming.value) return;

    // 添加用户消息
    const userMessage: ChatMessage = {
      id: generateId(),
      role: 'user',
      content: content.trim(),
      timestamp: Date.now()
    };
    messages.value.push(userMessage);

    // 创建助手消息占位
    const assistantMessage: ChatMessage = {
      id: generateId(),
      role: 'assistant',
      content: '',
      timestamp: Date.now()
    };
    messages.value.push(assistantMessage);
    currentAssistantMessage.value = assistantMessage;

    isStreaming.value = true;

    try {
      await sendSSERequest(
        getChatMessagesUrl(),
        {
          query: content.trim(),
          conversation_id: conversationId.value
        },
        {
          onMessage: (text) => {
            if (currentAssistantMessage.value) {
              currentAssistantMessage.value.content += text;
            }
          },
          onEnd: async (convId) => {
            conversationId.value = convId;
            // 保存会话ID到后端
            try {
              await saveConversation(convId);
            } catch (e) {
              console.error('Failed to save conversation:', e);
            }
          },
          onError: (error) => {
            console.error('SSE error:', error);
            if (currentAssistantMessage.value) {
              currentAssistantMessage.value.content = '抱歉，发生了错误，请稍后重试。';
            }
          }
        }
      );
    } catch (error) {
      console.error('Failed to send message:', error);
      if (currentAssistantMessage.value) {
        currentAssistantMessage.value.content = '网络错误，请检查网络连接后重试。';
      }
    } finally {
      isStreaming.value = false;
      currentAssistantMessage.value = null;
    }
  }

  // 清空会话
  function clearConversation() {
    conversationId.value = null;
    messages.value = [];
    currentAssistantMessage.value = null;
  }

  return {
    conversationId,
    messages,
    isStreaming,
    sendMessage,
    clearConversation
  };
});
