import { storage } from './storage';
import type { SSEEvent } from '@/types/chat';

export interface SSEOptions {
  onMessage: (content: string) => void;
  onEnd: (conversationId: string) => void;
  onError: (error: Error) => void;
}

/**
 * 发送SSE请求并处理流式响应
 */
export async function sendSSERequest(
  url: string,
  body: Record<string, unknown>,
  options: SSEOptions
): Promise<void> {
  const token = storage.getToken();
  
  const response = await fetch(url, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': token ? `Bearer ${token}` : ''
    },
    body: JSON.stringify(body)
  });

  if (!response.ok) {
    if (response.status === 401) {
      storage.clearAuth();
      window.location.href = '/';
      return;
    }
    throw new Error(`HTTP error! status: ${response.status}`);
  }

  const reader = response.body?.getReader();
  if (!reader) {
    throw new Error('Response body is not readable');
  }

  const decoder = new TextDecoder();
  let buffer = '';

  try {
    while (true) {
      const { done, value } = await reader.read();
      
      if (done) {
        break;
      }

      buffer += decoder.decode(value, { stream: true });
      const lines = buffer.split('\n\n');
      buffer = lines.pop() || '';

      for (const line of lines) {
        // 支持 "data: " 和 "data:" 两种格式
        let jsonStr = '';
        if (line.startsWith('data: ')) {
          jsonStr = line.slice(6).trim();
        } else if (line.startsWith('data:')) {
          jsonStr = line.slice(5).trim();
        }
        
        if (jsonStr) {
          try {
            const data = JSON.parse(jsonStr) as SSEEvent;
            handleSSEEvent(data, options);
          } catch (e) {
            console.error('Failed to parse SSE data:', e, 'Raw:', jsonStr.substring(0, 100));
          }
        } else if (line.trim()) {
          console.log('Unrecognized SSE line:', line.substring(0, 100));
        }
      }
    }

    // 处理剩余buffer
    let remainingJson = '';
    if (buffer.startsWith('data: ')) {
      remainingJson = buffer.slice(6).trim();
    } else if (buffer.startsWith('data:')) {
      remainingJson = buffer.slice(5).trim();
    }
    
    if (remainingJson) {
      try {
        const data = JSON.parse(remainingJson) as SSEEvent;
        handleSSEEvent(data, options);
      } catch (e) {
        console.error('Failed to parse remaining SSE data:', e);
      }
    }
  } finally {
    reader.releaseLock();
  }
}

function handleSSEEvent(data: SSEEvent, options: SSEOptions): void {
  console.log('SSE Event received:', data.event, data);
  switch (data.event) {
    case 'message':
      console.log('Message content:', data.answer);
      options.onMessage(data.answer);
      break;
    case 'message_end':
      options.onEnd(data.conversation_id);
      break;
    case 'error':
      options.onError(new Error(data.message));
      break;
    default:
      // 忽略其他事件类型 (workflow_started, node_started, node_finished 等)
      console.log('Ignored event:', data.event);
  }
}

/**
 * 生成唯一ID
 */
export function generateId(): string {
  return `${Date.now()}-${Math.random().toString(36).slice(2, 11)}`;
}
