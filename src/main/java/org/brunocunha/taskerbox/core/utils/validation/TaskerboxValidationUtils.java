package org.brunocunha.taskerbox.core.utils.validation;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

/**
 * Taskerbox Validation Utilities
 * 
 * @author Bruno Candido Volpato da Cunha
 * 
 */
public class TaskerboxValidationUtils {

	/**
	 * Execute validations in object
	 * @param object
	 * @throws IllegalArgumentException
	 */
	public static <T extends Object> void validate(T object) throws IllegalArgumentException {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();
		Set<ConstraintViolation<T>> valRes = validator.validate(object);
		if (!valRes.isEmpty()) {
			StringBuilder sb = new StringBuilder("Validation failed for: ");
			sb.append(object);

			for (ConstraintViolation<T> fail : valRes) {
				sb.append("\n  ").append(fail.getPropertyPath() + " - " + fail.getMessage());
			}
			throw new IllegalArgumentException(sb.toString());
		}
	}

}
