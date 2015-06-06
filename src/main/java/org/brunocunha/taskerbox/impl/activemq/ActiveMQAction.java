package org.brunocunha.taskerbox.impl.activemq;

import javax.jms.Message;

import lombok.extern.log4j.Log4j;

import org.apache.activemq.command.ActiveMQTextMessage;
import org.brunocunha.taskerbox.core.DefaultTaskerboxAction;

@Log4j
public class ActiveMQAction extends DefaultTaskerboxAction<Message>{

	@Override
	public void action(Message message) {
		try {
			if (message instanceof ActiveMQTextMessage) {
				ActiveMQTextMessage mqTextMessage = (ActiveMQTextMessage) message;
				logInfo(log, "Received MQ Message: " + mqTextMessage.getText());
			} else {
				logInfo(log, "Received Default Message: " + message);
			}
		} catch(Exception e) {
			logError(log, "Error on Action", e);
		}
	}

}
