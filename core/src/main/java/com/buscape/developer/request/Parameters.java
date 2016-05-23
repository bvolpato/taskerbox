/**
 * Copyright (C) 2015 Bruno Candido Volpato da Cunha (brunocvcunha@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.buscape.developer.request;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public final class Parameters {

  private Integer categoryId;

  private Integer productId;

  private Integer sellerId;

  private String keyword;

  private String barcode;


  /**
   * @return the categoryId
   */
  public final Integer getCategoryId() {
    return this.categoryId;
  }

  /**
   * @param categoryId the categoryId to set
   */
  public final void setCategoryId(Integer categoryId) {
    this.categoryId = categoryId;
  }

  /**
   * @return the productId
   */
  public final Integer getProductId() {
    return this.productId;
  }

  /**
   * @param productId the productId to set
   */
  public final void setProductId(Integer productId) {
    this.productId = productId;
  }

  /**
   * @return the sellerId
   */
  public final Integer getSellerId() {
    return this.sellerId;
  }

  /**
   * @param sellerId the sellerId to set
   */
  public final void setSellerId(Integer sellerId) {
    this.sellerId = sellerId;
  }

  /**
   * @return the keyword
   */
  public final String getKeyword() {
    return this.keyword;
  }

  /**
   * @param keyword the keyword to set
   */
  public final void setKeyword(String keyword) {
    this.keyword = keyword;
  }

  /**
   * @return the barcode
   */
  public final String getBarcode() {
    return this.barcode;
  }

  /**
   * @param barcode the barcode to set
   */
  public final void setBarcode(String barcode) {
    this.barcode = barcode;
  }

  /**
   * Build a {@link Map} that represents this instance. The pair key/value of map are the name of
   * fields in object and the values of fields, respectively.
   *
   * @return a {@link Map} populated with the values of this instance.
   */
  public Map<String, Object> asMap() {
    Map<String, Object> result = new HashMap<>();

    for (Field field : getClass().getDeclaredFields()) {
      try {
        String fieldName = field.getName();
        Method getter = getClass().getMethod(buildGetterName(fieldName));
        Object fieldValue = getter.invoke(this);
        if (fieldValue != null) {
          result.put(fieldName, fieldValue);
        }
      } catch (Exception ignored) {
      }
    }

    return result;
  }

  private String buildGetterName(String fieldName) {
    return "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1); //$NON-NLS-1$
  }
}
