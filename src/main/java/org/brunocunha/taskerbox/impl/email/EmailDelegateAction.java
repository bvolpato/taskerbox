package org.brunocunha.taskerbox.impl.email;

import java.io.File;

import lombok.Getter;
import lombok.Setter;

import org.brunocunha.taskerbox.core.DefaultTaskerboxAction;

public abstract class EmailDelegateAction<T> extends DefaultTaskerboxAction<T> {

	@Getter @Setter
	private String email;

	@Getter @Setter
	private String smtpHost;

	@Getter @Setter
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
	private File logFile;
	
	@Getter @Setter
	private boolean showToaster = true;

	@Getter @Setter
	private String emailTitle = "Email de Taskerbox";

	protected EmailAction getEmailAction() {
		EmailAction email = new EmailAction();
		email.setEmail(this.email);
		email.setEnableSSL(this.enableSSL);
		email.setEnableTLS(this.enableTLS);
		email.setSmtpFrom(this.smtpFrom);
		email.setSmtpHost(this.smtpHost);
		email.setSmtpPassword(this.smtpPassword);
		email.setSmtpPort(this.smtpPort);
		email.setSmtpUser(this.smtpUser);
		email.setLogFile(this.logFile);
		email.setShowToaster(this.showToaster);
		email.setEmailTitle(this.emailTitle);
		
		return email;
	}
}
