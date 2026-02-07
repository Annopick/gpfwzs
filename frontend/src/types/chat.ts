// 消息角色
export type MessageRole = 'user' | 'assistant';

// 聊天消息
export interface ChatMessage {
  id: string;
  role: MessageRole;
  content: string;
  timestamp: number;
}

// 发送消息请求
export interface ChatRequest {
  query: string;
  conversation_id?: string;
}

// SSE事件类型
export type SSEEventType = 'message' | 'message_end' | 'error' | 'workflow_started' | 'workflow_finished' | 'node_started' | 'node_finished';

// SSE消息事件
export interface SSEMessageEvent {
  event: 'message';
  answer: string;
  conversation_id: string;
}

// SSE结束事件
export interface SSEMessageEndEvent {
  event: 'message_end';
  conversation_id: string;
  metadata?: Record<string, unknown>;
}

// SSE错误事件
export interface SSEErrorEvent {
  event: 'error';
  code: string;
  message: string;
}

// 其他 Dify 事件（忽略处理）
export interface SSEOtherEvent {
  event: 'workflow_started' | 'workflow_finished' | 'node_started' | 'node_finished';
  conversation_id?: string;
  [key: string]: unknown;
}

// SSE事件联合类型
export type SSEEvent = SSEMessageEvent | SSEMessageEndEvent | SSEErrorEvent | SSEOtherEvent;
