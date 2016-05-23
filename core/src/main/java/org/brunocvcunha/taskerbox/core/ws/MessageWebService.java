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
package org.brunocvcunha.taskerbox.core.ws;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import lombok.extern.log4j.Log4j;

/**
 * Default WebService Channel to produce Taskerbox flows using SOAP calls
 *
 * @author Bruno Candido Volpato da Cunha
 *
 */
@WebService(name = "Message", serviceName = "MessageService", portName = "MessagePort")
@SOAPBinding(style = SOAPBinding.Style.RPC)
@Log4j
public class MessageWebService extends TaskerboxWebService<String> {

  @WebMethod(operationName = "sendMessage")
  public String sendMessage(@WebParam(name = "msg") String msg) {
    log.info("Performing actions for message '" + msg + "'");

    getChannel().perform(msg);

    return "Mesage Processed";
  }

}
