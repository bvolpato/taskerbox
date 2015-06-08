package org.brunocunha.taskerbox.impl.email;

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
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

import org.brunocunha.taskerbox.core.DefaultTaskerboxAction;
import org.brunocunha.taskerbox.core.ITaskerboxEmailable;
import org.brunocunha.taskerbox.core.http.ssl.SSLAuthenticator;
import org.brunocunha.taskerbox.core.utils.TaskerboxDateUtils;
import org.brunocunha.taskerbox.impl.toaster.StringToasterAction;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Action that shows Feeds in a Toaster Popup
 * 
 * @author Bruno Candido Volpato da Cunha
 * 
 */
@Log4j
public class EmailAction extends DefaultTaskerboxAction<Object> {

	@Email
	@NotEmpty
	@Getter @Setter
	private String email;

	@NotEmpty
	@Getter @Setter
	private String smtpHost;

	@Getter @Setter
	@Min(1) @Max(65535)
	private int smtpPort;

	@Getter @Setter
	private String smtpFrom;

	@Getter @Setter
	private String smtpUser;

	@Getter @Setter
	private String smtpPassword;

	@Getter @Setter
	private boolean enableTLS;

	@Getter @Setter
	private boolean enableSSL;

	@Getter @Setter
	private boolean showToaster = true;

	@Getter @Setter
	private File logFile;

	@Getter @Setter
	private String emailTitle = "Email de Taskerbox";
	
	/*
	 * public static void main(String[] args) { Properties prop = new
	 * Properties(); prop.put("title", "titulo"); prop.put("content",
	 * "titulo explanado");
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
			vo.setTitle(emailTitle);
			vo.setBody(entry.toString());
			
			handleEmailValue(vo);

		}

	}

	private void handleEmailable(ITaskerboxEmailable entry) {
		
		try {
			send(entry.getEmailTitle(getChannel()), entry.getEmailBody(getChannel()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}



	private void handleEmailValue(EmailValueVO email) {

		try {
			send(email.getTitle(), email.getBody());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	

	public void send(String title, String content) throws AddressException, MessagingException, KeyManagementException,
			NoSuchAlgorithmException {

		Properties props = System.getProperties();

		SSLAuthenticator sslAuthenticator = null;
		if (enableSSL) {
			log.debug("Using SSL to Connection with " + smtpHost + ":" + smtpPort);

			sslAuthenticator = new SSLAuthenticator(smtpUser, smtpPassword);

			SSLContext ctx = SSLContext.getInstance("TLS");
			ctx.init(new KeyManager[0], new TrustManager[] { new SSLAuthenticator.DefaultTrustManager() },
					new SecureRandom());
			SSLContext.setDefault(ctx);

			props.setProperty("mail.smtp.submitter", sslAuthenticator.getPasswordAuthentication().getUserName());
			props.setProperty("mail.smtp.auth", "true");
		}

		props.setProperty("mail.smtp.host", smtpHost);
		props.setProperty("mail.smtp.port", String.valueOf(smtpPort));
		props.setProperty("mail.smtp.starttls", String.valueOf(enableTLS));
		props.setProperty("mail.smtp.starttls.enable", "true");

		Session session;

		if (smtpPassword != null && !smtpPassword.equals("")) {
			props.setProperty("mail.smtp.auth", "true");

			Authenticator auth = new Authenticator() {
				public PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(smtpUser, smtpPassword);
				}
			};

			if (enableSSL) {
				log.debug("Opening SSL Session...");

				session = Session.getInstance(props, sslAuthenticator);
			} else {
				session = Session.getInstance(props, auth);
			}
		} else {
			session = Session.getInstance(props);
		}


		
		if (logFile != null) {
			try {
				FileWriter out = new FileWriter(logFile, true);
				out.write("=========================================\r\n");
				out.write(TaskerboxDateUtils.getTimestamp() + " Assunto: " + title + "\r\n");
				out.write(content + "\r\n");
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
		String msg = "Sending email to: "+ email + " (Channel: [channel]): " + title;
		if (getChannel() != null) {
			msg = msg.replace("[channel]", getChannel().getId());
		}
		logInfo(log, "Sending email: " + msg);
		
		if (showToaster) {
			new StringToasterAction().action(msg);
		}
		
		MimeMessage message = new MimeMessage(session);
		message.setFrom(new InternetAddress(smtpFrom));
		message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
		message.setSubject(title);
		message.setContent(content, "text/html");

		Transport.send(message);

	}

	
}
