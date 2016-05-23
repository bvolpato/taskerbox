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
package org.brunocvcunha.taskerbox.impl.soap;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.ws.Endpoint;

import org.brunocvcunha.taskerbox.core.ITaskerboxAction;
import org.brunocvcunha.taskerbox.core.TaskerboxChannel;
import org.brunocvcunha.taskerbox.core.ws.MessageWebService;
import org.brunocvcunha.taskerbox.core.ws.TaskerboxWebService;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.URL;

import lombok.extern.log4j.Log4j;

@WebService(name = "SOAP", serviceName = "SOAPService", portName = "SOAPPort")
@SOAPBinding(style = SOAPBinding.Style.RPC)
@Log4j
public class SOAPChannel<T> extends TaskerboxChannel<T> {

  @NotEmpty
  @URL
  private String wsAddress;

  private Class<? extends TaskerboxWebService> wsClass = MessageWebService.class;

  public SOAPChannel() {
    this.singleItemAction = false;
  }

  @WebMethod(operationName = "sendMessage")
  public String sendMessage(@WebParam(name = "msg") T msg) {
    log.debug("Performing actions to message");

    for (ITaskerboxAction<T> action : this.getActions()) {
      action.action(msg);
    }

    return msg.hashCode() + " received.";
  }

  @Override
  @WebMethod(exclude = true)
  protected void execute() throws Exception {

    TaskerboxWebService<T> wsImpl = this.wsClass.newInstance();
    wsImpl.setChannel(this);

    Endpoint.publish(this.wsAddress, wsImpl);

    logInfo(log, "Web service '" + this.wsClass.getName() + "' was published successfully.\n"
        + "WSDL URL: " + this.wsAddress + "?WSDL");

    // Keep the local web server running until the process is killed
    while (Thread.currentThread().isAlive()) {
      try {
        Thread.sleep(10000);
      } catch (InterruptedException ex) {
      }
    }

  }

  @Override
  @WebMethod(exclude = true)
  public String getItemFingerprint(T entry) {
    return entry.toString();
  }

  @WebMethod(exclude = true)
  public String getWsAddress() {
    return this.wsAddress;
  }

  @WebMethod(exclude = true)
  public void setWsAddress(String wsAddress) {
    this.wsAddress = wsAddress;
  }

  public Class<? extends TaskerboxWebService> getWsClass() {
    return this.wsClass;
  }

  public void setWsClass(Class<? extends TaskerboxWebService> wsClass) {
    this.wsClass = wsClass;
  }

}
