package it.vige.labs.gc.websocket;

import static it.vige.labs.gc.JavaAppApplication.BROKER_V_NAME;
import static it.vige.labs.gc.JavaAppApplication.TOPIC_V_NAME;
import static java.security.KeyStore.getInstance;
import static org.apache.tomcat.websocket.Constants.SSL_CONTEXT_PROPERTY;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.SecureRandom;

import javax.annotation.PostConstruct;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509KeyManager;
import javax.websocket.ContainerProvider;
import javax.websocket.WebSocketContainer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import it.vige.labs.gc.bean.votingpapers.VotingPapers;
import it.vige.labs.gc.rest.Validator;

@Service
public class WebSocketVotingPapersClient {

	private WebSocketContainer webSocketContainer = ContainerProvider.getWebSocketContainer();

	private StandardWebSocketClient standardWebSocketClient = new StandardWebSocketClient(webSocketContainer);

	private WebSocketStompClient stompClient = new WebSocketStompClient(standardWebSocketClient);

	private StompSession stompSession;
	
	@Autowired
	private Validator validator;

	@Value("${votingpapers.port}")
	private int serverPort;

	@Value("${websocket.scheme}")
	private String websocketScheme;

	@Value("${votingpapers.host}")
	private String serverHost;

	@Value("${server.ssl.key-store:#{null}}")
	private String keystoreFile;

	@Value("${server.ssl.key-store-password:#{null}}")
	private String keystorePass;

	@Value("${server.ssl.keyStoreType:#{null}}")
	private String keystoreType;

	@Value("${server.ssl.keyAlias:#{null}}")
	private String keyAlias;

	@PostConstruct
	public void init() throws Exception {
		connect();
	}

	private void connect() throws Exception {
		if (websocketScheme.equals("wss")) {
			SSLContext sslContext = sslContext(keystoreFile, keystorePass);
			standardWebSocketClient.getUserProperties().put(SSL_CONTEXT_PROPERTY, sslContext);
		}
		webSocketContainer.setDefaultMaxBinaryMessageBufferSize(100000000);
		webSocketContainer.setDefaultMaxTextMessageBufferSize(100000000);
		stompClient.setMessageConverter(new MappingJackson2MessageConverter());
		stompClient.setInboundMessageSizeLimit(100000000);
		StompSessionHandlerAdapter stompSessionHandlerAdapter = new StompSessionHandlerAdapter() {

			@Override
			public Type getPayloadType(StompHeaders headers) {
				return VotingPapers.class;
			}
			
			@Override
			public void handleFrame(StompHeaders headers, Object payload) {
				validator.setVotingPapers((VotingPapers)payload);
			}

		};
		stompSession = stompClient.connect(websocketScheme + "://" + serverHost + ":" + serverPort + BROKER_V_NAME,
				stompSessionHandlerAdapter).get();
		stompSession.subscribe(TOPIC_V_NAME, stompSessionHandlerAdapter);
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
