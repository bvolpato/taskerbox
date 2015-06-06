package org.brunocunha.taskerbox.core.utils.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Implemented Correios Tracking Validator 
 * @author Bruno Candido Volpato da Cunha
 *
 */
public class CorreiosTrackingValidator implements ConstraintValidator<CorreiosTracking, String> {

	private Pattern pattern = Pattern.compile("\\w{2}\\d{9}\\w{2}");

	@Override
	public void initialize(CorreiosTracking constraintAnnotation) {

	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if (value == null) {
			return false;
		}
		Matcher m = pattern.matcher(value);
		return m.matches();
	}

}