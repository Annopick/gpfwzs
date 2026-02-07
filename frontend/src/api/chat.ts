import { http } from '@/utils/request';

/**
 * 保存会话ID
 */
export function saveConversation(conversationId: string): Promise<void> {
  return http.post('/chat/conversations', { conversation_id: conversationId });
}

/**
 * 获取聊天消息API地址
 */
export function getChatMessagesUrl(): string {
  const baseUrl = import.meta.env.VITE_API_BASE_URL || '/api';
  return `${baseUrl}/chat/messages`;
}
