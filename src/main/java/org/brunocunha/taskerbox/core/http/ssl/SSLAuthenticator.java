package org.brunocunha.taskerbox.core.http.ssl;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.mail.PasswordAuthentication;
import javax.net.ssl.X509TrustManager;

/**
 * SSL Authenticator for Mail API
 * 
 */
public class SSLAuthenticator extends javax.mail.Authenticator {

	private PasswordAuthentication authentication;

	public SSLAuthenticator(String userName, String password) {
		authentication = new PasswordAuthentication(userName, password);
	}

	public PasswordAuthentication getPasswordAuthentication() {
		return authentication;
	}

	public static class DefaultTrustManager implements X509TrustManager {

		@Override
		public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
		}

		@Override
		public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
		}

		@Override
		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}
	}

}
