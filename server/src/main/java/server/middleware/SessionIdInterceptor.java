package server.middleware;

import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Component
public class SessionIdInterceptor implements ChannelInterceptor {

    /**
     * Before a client to server message is received by the appropriate controller, a header containing
     * the sessionId is added to the message for easier access
     *
     * @param message - the intercepted message
     * @param channel - the channel used to send the message
     * @return the modified message with the sessionId header added
     */
    @Override
    public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && accessor.isMutable()) {
            String sessionId = accessor.getSessionId();
            accessor.setHeader("sessionId", sessionId);
        }

        return message;
    }

}

