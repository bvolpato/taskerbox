package org.brunocunha.taskerbox.impl.tracking;

import java.util.Properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import org.alfredlibrary.utilitarios.correios.RegistroRastreamento;
import org.brunocunha.taskerbox.core.ITaskerboxEmailable;
import org.brunocunha.taskerbox.core.TaskerboxChannel;
import org.brunocunha.taskerbox.core.TaskerboxVelocityUtils;

/**
 * RegistroRastreamento Wrapper - Emailable
 * @author Bruno Candido Volpato da Cunha
 *
 */
@RequiredArgsConstructor
public class CorreiosTrackingWrapper implements ITaskerboxEmailable {

	@Getter @Setter
	private RegistroRastreamento value;
	
	public CorreiosTrackingWrapper(RegistroRastreamento value) {
		this.value = value;
	}

	@Override
	public String getEmailTitle(TaskerboxChannel<?> channel) {
		return "Tracking " + channel.getProperty("tracking") + " - " + channel.getProperty("descricao");
	}

	@Override
	public String getEmailBody(TaskerboxChannel<?> channel) {
		return  CorreiosChannel
				.formatTracking((RegistroRastreamento) value, channel.getProperty("tracking"),
						channel.getProperty("descricao"));
	}
}
