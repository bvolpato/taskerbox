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
package org.brunocvcunha.taskerbox.impl.email;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.brunocvcunha.taskerbox.core.DefaultTaskerboxAction;
import org.brunocvcunha.taskerbox.core.ITaskerboxEmailable;
import org.brunocvcunha.taskerbox.core.http.ssl.SSLAuthenticator;
import org.brunocvcunha.taskerbox.core.utils.TaskerboxDateUtils;
import org.brunocvcunha.taskerbox.impl.toaster.StringToasterAction;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

/**
 * Action that sends emails
 *
 * @author Bruno Candido Volpato da Cunha
 *
 */
@Log4j
public class EmailAction extends DefaultTaskerboxAction<Object> {

  @Email
  @NotEmpty
  @Getter
  @Setter
  private String email;

  @NotEmpty
  @Getter
  @Setter
  private String smtpHost;

  @Getter
  @Setter
  @Min(1)
  @Max(65535)
  private int smtpPort;

  @Getter
  @Setter
  private String smtpFrom;

  @Getter
  @Setter
  private String smtpUser;

  @Getter
  @Setter
  private String smtpPassword;

  @Getter
  @Setter
  private boolean enableTLS;

  @Getter
  @Setter
  private boolean enableSSL;

  @Getter
  @Setter
  private boolean showToaster = true;

  @Getter
  @Setter
  private File logFile;

  @Getter
  @Setter
  private String emailTitle;

  /*
   * public static void main(String[] args) { Properties prop = new Properties(); prop.put("title",
   * "titulo"); prop.put("content", "titulo explanado");
   *
   * System.out.println(callTemplate("synd.html", prop)); }
   */

  @Override
  public void action(final Object entry) {

    if (entry instanceof ITaskerboxEmailable) {

      handleEmailable((ITaskerboxEmailable) entry);

    } else if (entry instanceof EmailValueVO) {

      handleEmailValue((EmailValueVO) entry);

    } else {

      logWarn(log, "Email Unknown Action! " + entry.getClass() + " --- " + entry.toString());

      EmailValueVO vo = new EmailValueVO();
      vo.setTitle(this.emailTitle);
      vo.setBody(entry.toString());

      handleEmailValue(vo);

    }

  }

  private void handleEmailable(ITaskerboxEmailable entry) {

    try {
      String emailTitle = getEmailTitle();
      if (emailTitle == null) {
        emailTitle = entry.getEmailTitle(getChannel());
      }

      send(emailTitle, entry.getEmailBody(getChannel()));
    } catch (Exception e) {
      e.printStackTrace();
    }

  }



  private void handleEmailValue(EmailValueVO email) {

    try {
      String emailTitle = getEmailTitle();
      if (emailTitle == null) {
        emailTitle = email.getTitle();
      }

      send(emailTitle, email.getBody());
    } catch (Exception e) {
      e.printStackTrace();
    }

  }


  public void send(String title, String content) throws AddressException, MessagingException,
      KeyManagementException, NoSuchAlgorithmException {

    Properties props = System.getProperties();

    SSLAuthenticator sslAuthenticator = null;
    if (this.enableSSL) {
      log.debug("Using SSL to Connection with " + this.smtpHost + ":" + this.smtpPort);

      sslAuthenticator = new SSLAuthenticator(this.smtpUser, this.smtpPassword);

      SSLContext ctx = SSLContext.getInstance("TLS");
      ctx.init(null, new TrustManager[] {new SSLAuthenticator.DefaultTrustManager()},
          new SecureRandom());
      HttpsURLConnection.setDefaultSSLSocketFactory(ctx.getSocketFactory());
      SSLContext.setDefault(ctx);

      props.setProperty("mail.smtp.submitter", sslAuthenticator.getPasswordAuthentication()
          .getUserName());
      props.setProperty("mail.smtp.auth", "true");
    }

    props.setProperty("mail.smtp.host", this.smtpHost);
    props.setProperty("mail.smtp.port", String.valueOf(this.smtpPort));
    props.setProperty("mail.smtp.starttls", String.valueOf(this.enableTLS));
    props.setProperty("mail.smtp.starttls.enable", "true");

    Session session;

    if (this.smtpPassword != null && !this.smtpPassword.equals("")) {
      props.setProperty("mail.smtp.auth", "true");

      Authenticator auth = new Authenticator() {
        @Override
        public PasswordAuthentication getPasswordAuthentication() {
          return new PasswordAuthentication(EmailAction.this.smtpUser, EmailAction.this.smtpPassword);
        }
      };

      if (this.enableSSL) {
        log.debug("Opening SSL Session...");

        session = Session.getInstance(props, sslAuthenticator);
      } else {
        session = Session.getInstance(props, auth);
      }
    } else {
      session = Session.getInstance(props);
    }



    if (this.logFile != null) {
      try {
        FileWriter out = new FileWriter(this.logFile, true);
        out.write("=========================================\r\n");
        out.write(TaskerboxDateUtils.getTimestamp() + " Assunto: " + title + "\r\n");
        out.write(content + "\r\n");
        out.close();
      } catch (IOException e) {
        e.printStackTrace();
      }

    }

    String msg = "Sending email to: " + this.email + " (Channel: [channel]): " + title;
    if (getChannel() != null) {
      msg = msg.replace("[channel]", getChannel().getId());
    }
    logInfo(log, "Sending email: " + msg);

    if (this.showToaster) {
      new StringToasterAction().action(msg);
    }

    MimeMessage message = new MimeMessage(session);
    message.setFrom(new InternetAddress(this.smtpFrom));
    message.addRecipient(Message.RecipientType.TO, new InternetAddress(this.email));
    message.setSubject(title);
    message.setContent(content, "text/html");

    Transport.send(message);

  }


}
