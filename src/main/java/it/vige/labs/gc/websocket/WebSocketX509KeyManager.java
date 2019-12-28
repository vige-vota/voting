package it.vige.labs.gc.websocket;

import java.net.Socket;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509KeyManager;

public class WebSocketX509KeyManager implements X509KeyManager {

	private X509KeyManager origKm;
	private String keyAlias;

	public WebSocketX509KeyManager(X509KeyManager origKm, String keyAlias) {
		this.origKm = origKm;
		this.keyAlias = keyAlias;
	}

	@Override
	public String chooseClientAlias(String[] keyType, Principal[] issuers, Socket socket) {
		return keyAlias;
	}

	@Override
	public X509Certificate[] getCertificateChain(String alias) {
		return origKm.getCertificateChain(alias);
	}

	@Override
	public String chooseServerAlias(String arg0, Principal[] arg1, Socket arg2) {
		return null;
	}

	@Override
	public String[] getClientAliases(String arg0, Principal[] arg1) {
		return null;
	}

	@Override
	public PrivateKey getPrivateKey(String arg0) {
		return null;
	}

	@Override
	public String[] getServerAliases(String arg0, Principal[] arg1) {
		return null;
	}
}
