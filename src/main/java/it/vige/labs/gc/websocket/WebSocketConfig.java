package it.vige.labs.gc.websocket;

import static it.vige.labs.gc.JavaAppApplication.BROKER_NAME;
import static it.vige.labs.gc.JavaAppApplication.BROKER_V_NAME;
import static java.lang.Integer.MAX_VALUE;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;
import org.springframework.web.socket.server.RequestUpgradeStrategy;
import org.springframework.web.socket.server.standard.TomcatRequestUpgradeStrategy;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

	/**
	 * Register Stomp endpoints: the url to open the WebSocket connection.
	 */
	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		RequestUpgradeStrategy upgradeStrategy = new TomcatRequestUpgradeStrategy();
		registry.addEndpoint(BROKER_NAME).setAllowedOriginPatterns("*").withSockJS();

		registry.addEndpoint(BROKER_NAME).setHandshakeHandler(new DefaultHandshakeHandler(upgradeStrategy))
				.setAllowedOriginPatterns("*");
		
		registry.addEndpoint(BROKER_V_NAME).setAllowedOriginPatterns("*").withSockJS();

		registry.addEndpoint(BROKER_V_NAME).setHandshakeHandler(new DefaultHandshakeHandler(upgradeStrategy))
				.setAllowedOriginPatterns("*");
	}
	
    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
        registration.setMessageSizeLimit(MAX_VALUE); // default : 64 * 1024
        registration.setSendTimeLimit(MAX_VALUE); // default : 10 * 10000
        registration.setSendBufferSizeLimit(MAX_VALUE); // default : 512 * 1024
    }

}