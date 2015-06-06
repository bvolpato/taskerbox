package org.brunocunha.taskerbox.impl.soap;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.ws.Endpoint;

import lombok.extern.log4j.Log4j;

import org.brunocunha.taskerbox.core.ITaskerboxAction;
import org.brunocunha.taskerbox.core.TaskerboxChannel;
import org.brunocunha.taskerbox.core.ws.MessageWebService;
import org.brunocunha.taskerbox.core.ws.TaskerboxWebService;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.URL;

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
	@WebMethod(exclude=true)
	protected void execute() throws Exception {

		TaskerboxWebService<T> wsImpl = wsClass.newInstance();
		wsImpl.setChannel(this);
		
		Endpoint.publish(wsAddress, wsImpl);
		
		logInfo(log, "Web service '" + wsClass.getName() + "' was published successfully.\n"
				+ "WSDL URL: " + wsAddress + "?WSDL");
		
		// Keep the local web server running until the process is killed
		while (Thread.currentThread().isAlive()) {
			try {
				Thread.sleep(10000);
			} catch (InterruptedException ex) {
			}
		}

	}

	@Override
	@WebMethod(exclude=true)
	public String getItemFingerprint(T entry) {
		return entry.toString();
	}
	
	@WebMethod(exclude=true)
	public String getWsAddress() {
		return wsAddress;
	}

	@WebMethod(exclude=true)
	public void setWsAddress(String wsAddress) {
		this.wsAddress = wsAddress;
	}

	public Class<? extends TaskerboxWebService> getWsClass() {
		return wsClass;
	}

	public void setWsClass(Class<? extends TaskerboxWebService> wsClass) {
		this.wsClass = wsClass;
	}
	
}