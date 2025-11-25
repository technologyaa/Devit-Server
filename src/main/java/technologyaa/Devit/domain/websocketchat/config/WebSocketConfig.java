package technologyaa.Devit.domain.websocketchat.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * WebSocket 활성화 및 설정을 위한 클래스
 * 
 * @see ChatHandler
 */
@Configuration
@EnableWebSocket // Spring WebSocket 활성화
public class WebSocketConfig implements WebSocketConfigurer {

    private final ChatHandler chatHandler;

    @Autowired
    public WebSocketConfig(ChatHandler chatHandler) {
        this.chatHandler = chatHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // "/ws/chat" 엔드포인트에 ChatHandler를 등록합니다.
        // setAllowedOrigins("*")는 CORS 설정을 간소화합니다. (운영 환경에서는 특정 도메인만 허용해야 합니다.)
        registry.addHandler(chatHandler, "/ws/chat")
                .setAllowedOrigins("*");
    }
}
