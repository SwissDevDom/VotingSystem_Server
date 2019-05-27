package ch.fhnw.kvanc.server.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;

@Component
public class WebSocketEndpoint extends TextWebSocketHandler {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    private WebSocketSession session = null;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception{
        this.session = session;
        log.debug("Established new session " + session.getId());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status)throws Exception{
        super.afterConnectionClosed(session, status);
        log.debug("Closed session " + session.getId());
        this.session = null;
    }

    public void pushMessage(String message) throws IOException {
        // your code:
        // - create a websocket text-message. Serialize it if needed.
        // - send it using the session instance
        if (session != null) {
            TextMessage text = new TextMessage(message);
            session.sendMessage(text);
            log.debug("Sent textmessage to session " + session.getId());
        } else {
            log.debug("No session available to push message");
        }
    }

    public void reset() throws IOException {
        if (session != null) {
            log.debug("Session '{}' closed", session.getId());
            session.close(CloseStatus.SERVICE_RESTARTED);
        }
    }
}
