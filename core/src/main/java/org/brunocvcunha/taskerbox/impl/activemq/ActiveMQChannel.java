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
package org.brunocvcunha.taskerbox.impl.activemq;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.brunocvcunha.taskerbox.core.TaskerboxChannel;
import org.brunocvcunha.taskerbox.core.annotation.TaskerboxField;

import lombok.Getter;
import lombok.Setter;

public class ActiveMQChannel extends TaskerboxChannel<Message> {

  @Getter
  @Setter
  @TaskerboxField("Connection")
  private String connectionString;

  @Getter
  @Setter
  @TaskerboxField("Queue/Topic Name")
  private String queueName;

  @Getter
  @Setter
  @TaskerboxField("Use Topic")
  private boolean topicSchema;

  @Getter
  private boolean isAlive;

  public static void main(String[] args) throws Exception {
    ActiveMQChannel channel = new ActiveMQChannel();
    channel.setConnectionString("tcp://localhost:61616");
    channel.setQueueName("teste");
    channel.execute();
  }

  @Override
  protected void execute() throws Exception {
    try {
      this.isAlive = true;

      // Create a ConnectionFactory
      ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(this.connectionString);

      // Create a Connection
      Connection connection = connectionFactory.createConnection();
      connection.start();

      connection.setExceptionListener(new ExceptionListener() {
        @Override
        public synchronized void onException(JMSException ex) {
          System.err.println("JMS Exception occured: " + ex.getMessage());
          ex.printStackTrace();

          ActiveMQChannel.this.isAlive = false;
        }
      });

      // Create a Session
      Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

      // Create the destination (Topic or Queue)
      Destination destination;
      if (this.topicSchema) {
        destination = session.createTopic(this.queueName);
      } else {
        destination = session.createQueue(this.queueName);
      }

      // Create a MessageConsumer from the Session to the Topic or Queue
      MessageConsumer consumer = session.createConsumer(destination);

      MessageListener messageListener = new MessageListener() {
        @Override
        public void onMessage(Message message) {
          try {
            perform(message);
          } catch (Exception e) {
            System.out.println("Caught:" + e);
            e.printStackTrace();
          }
        }
      };

      consumer.setMessageListener(messageListener);

      // consumer.receive()
      /*
       * consumer.close(); session.close(); connection.close();
       */


      // Checks every 5s if the thread is still alive
      while (!isPaused() && this.isAlive) {
        Thread.sleep(5000L);
      }

    } catch (Exception e) {
      System.out.println("Caught: " + e);
      e.printStackTrace();
    }
  }

  @Override
  protected String getItemFingerprint(Message entry) {
    try {
      return this.queueName + "-" + entry.getJMSMessageID();
    } catch (JMSException e) {
      e.printStackTrace();
    }

    return this.queueName + "-" + entry.toString();
  }
}
