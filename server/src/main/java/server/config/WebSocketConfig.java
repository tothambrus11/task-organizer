package server.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import server.middleware.SessionIdInterceptor;

import static commons.Constants.WEBSOCKET_PATH;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final SessionIdInterceptor sessionIdInterceptor;

    @Autowired
    public WebSocketConfig(SessionIdInterceptor sessionIdInterceptor) {
        this.sessionIdInterceptor = sessionIdInterceptor;
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint(WEBSOCKET_PATH);
    }

    /**
     * Intercepts messages sent from the client and adds a sessionId header before the message is received
     * by the appropriate controller
     *
     * @param registration - the ChannelRegistration object used for adding an interceptor to the client inbound channel
     */
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(sessionIdInterceptor);
    }
}
