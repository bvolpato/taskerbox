/**
 * Copyright (C) 2015 Bruno Candido Volpato da Cunha (brunocvcunha@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.brunocvcunha.taskerbox.core.utils.validation;

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
   *
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
