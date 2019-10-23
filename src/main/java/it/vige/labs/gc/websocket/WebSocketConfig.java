package it.vige.labs.gc.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.RequestUpgradeStrategy;
import org.springframework.web.socket.server.standard.TomcatRequestUpgradeStrategy;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import it.vige.labs.gc.JavaAppApplication;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

	/**
	 * Register Stomp endpoints: the url to open the WebSocket connection.
	 */
	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		RequestUpgradeStrategy upgradeStrategy = new TomcatRequestUpgradeStrategy();
		registry.addEndpoint(JavaAppApplication.BROKER_NAME).setAllowedOrigins("*").withSockJS();

		registry.addEndpoint(JavaAppApplication.BROKER_NAME)
				.setHandshakeHandler(new DefaultHandshakeHandler(upgradeStrategy)).setAllowedOrigins("*");
	}

}