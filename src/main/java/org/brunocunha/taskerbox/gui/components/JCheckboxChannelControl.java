package org.brunocunha.taskerbox.gui.components;

import javax.swing.JCheckBox;

import lombok.Getter;
import lombok.Setter;

import org.brunocunha.taskerbox.core.TaskerboxChannel;

public class JCheckboxChannelControl extends JCheckBox {

	@Getter @Setter
	private TaskerboxChannel<?> channel;

}
