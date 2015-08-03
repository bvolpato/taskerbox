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

import org.brunocvcunha.taskerbox.core.DefaultTaskerboxAction;

import lombok.Getter;
import lombok.Setter;

public abstract class EmailDelegateAction<T> extends DefaultTaskerboxAction<T> {

  @Getter
  @Setter
  private String email;

  @Getter
  @Setter
  private String smtpHost;

  @Getter
  @Setter
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
  private File logFile;

  @Getter
  @Setter
  private boolean showToaster = true;

  @Getter
  @Setter
  private String emailTitle;

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
