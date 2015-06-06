package org.brunocunha.taskerbox.core.ws;

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
