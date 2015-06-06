package org.brunocunha.taskerbox.core.ws;

import lombok.Getter;
import lombok.Setter;

import org.brunocunha.taskerbox.impl.soap.SOAPChannel;


/**
 * POJO that ties the WebService to Channel
 * @author Bruno Candido Volpato da Cunha
 *
 * @param <T>
 */
public class TaskerboxWebService<T> {

	@Getter @Setter
	private SOAPChannel<T> channel;
	
}
