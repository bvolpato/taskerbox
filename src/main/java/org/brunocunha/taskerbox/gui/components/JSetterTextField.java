package org.brunocunha.taskerbox.gui.components;

import java.lang.reflect.Method;

import javax.swing.JTextField;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public class JSetterTextField extends JTextField {

	/**
	 * 
	 */
	private static final long serialVersionUID = 357697137847233150L;
	
	@Getter @Setter
	private Method setter;
	
}
