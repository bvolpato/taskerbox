/**
 * Copyright (C) 2015 Bruno Candido Volpato da Cunha (brunocvcunha@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.brunocvcunha.taskerbox.core.http.ssl;

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
    this.authentication = new PasswordAuthentication(userName, password);
  }

  @Override
public PasswordAuthentication getPasswordAuthentication() {
    return this.authentication;
  }

  public static class DefaultTrustManager implements X509TrustManager {

    @Override
    public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {}

    @Override
    public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {}

    @Override
    public X509Certificate[] getAcceptedIssuers() {
      return null;
    }
  }

}
