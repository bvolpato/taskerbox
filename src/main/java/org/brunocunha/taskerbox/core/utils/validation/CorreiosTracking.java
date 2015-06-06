package org.brunocunha.taskerbox.core.utils.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;


/**
 * Correios Tracking Validator Interface
 * @author Bruno Candido Volpato da Cunha
 *
 */
@Constraint(validatedBy = CorreiosTrackingValidator.class)
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CorreiosTracking {
   
  String message() default "Invalid Tracking";
  Class<?>[] groups() default { };
  Class<? extends Payload>[] payload() default { };
 
}
