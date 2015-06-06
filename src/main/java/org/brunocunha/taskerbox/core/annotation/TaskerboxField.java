package org.brunocunha.taskerbox.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation that means that the field is relevant to Taskerbox
 * Tells the Taskerbox that the field must be shown in UI
 * @author Bruno Candido Volpato da Cunha
 *
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TaskerboxField {
	String value();
	boolean readOnly() default false;
	
}
