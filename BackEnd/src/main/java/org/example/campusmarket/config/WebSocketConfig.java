package org.example.campusmarket.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker  // 关键：启用WebSocket消息代理，触发SimpMessagingTemplate的Bean注册
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 前端连接的WebSocket端点
        registry.addEndpoint("/ws/chat")  // 端点路径：ws://localhost:8080/ws/chat
                .setAllowedOriginPatterns("*")  // 允许跨域
                .withSockJS();  // 支持SockJS降级（兼容不支持WebSocket的浏览器）
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 启用简单消息代理，用于推送用户专属消息（路径前缀为/user）
        registry.enableSimpleBroker("/user");
        // 应用内消息的前缀（前端发送消息到后端时使用）
        registry.setApplicationDestinationPrefixes("/app");
    }
}