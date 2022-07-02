package it.vige.labs.gc.websocket;

import static it.vige.labs.gc.JavaAppApplication.BROKER_NAME;
import static java.security.KeyStore.getInstance;
import static org.apache.tomcat.websocket.Constants.SSL_CONTEXT_PROPERTY;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.SecureRandom;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509KeyManager;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

@Service
public class WebSocketClient {

	private StandardWebSocketClient standardWebSocketClient = new StandardWebSocketClient();

	private WebSocketStompClient stompClient = new WebSocketStompClient(standardWebSocketClient);

	private StompSession stompSession;

	@LocalServerPort
	private int serverPort;

	@Value("${websocket.scheme}")
	private String websocketScheme;

	@Value("${server.host}")
	private String serverHost;

	@Value("${server.ssl.key-store:#{null}}")
	private String keystoreFile;

	@Value("${server.ssl.key-store-password:#{null}}")
	private String keystorePass;

	@Value("${server.ssl.keyStoreType:#{null}}")
	private String keystoreType;

	@Value("${server.ssl.keyAlias:#{null}}")
	private String keyAlias;

	private void connect() throws Exception {
		if (websocketScheme.equals("wss")) {
			SSLContext sslContext = sslContext(keystoreFile, keystorePass);
			standardWebSocketClient.getUserProperties().put(SSL_CONTEXT_PROPERTY, sslContext);
		}
		stompClient.setMessageConverter(new MappingJackson2MessageConverter());
		stompSession = stompClient.connect(websocketScheme + "://" + serverHost + ":" + serverPort + BROKER_NAME,
				new StompSessionHandlerAdapter() {
				}).get();
	}

	public StompSession getStompSession() throws Exception {
		if (stompSession == null)
			connect();
		return stompSession;
	}

	private SSLContext sslContext(String keystoreFile, String password) throws GeneralSecurityException, IOException {
		KeyStore keystore = getInstance(keystoreType);
		try (InputStream in = new FileInputStream(keystoreFile)) {
			keystore.load(in, password.toCharArray());
		}
		KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
		keyManagerFactory.init(keystore, password.toCharArray());

		final X509KeyManager origKm = (X509KeyManager) keyManagerFactory.getKeyManagers()[0];
		X509KeyManager km = new WebSocketX509KeyManager(origKm, keyAlias);

		TrustManagerFactory trustManagerFactory = TrustManagerFactory
				.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		trustManagerFactory.init(keystore);

		SSLContext sslContext = SSLContext.getInstance("TLS");
		sslContext.init(new KeyManager[] { km }, trustManagerFactory.getTrustManagers(), new SecureRandom());

		return sslContext;
	}
}
