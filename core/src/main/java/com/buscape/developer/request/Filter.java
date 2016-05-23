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

/**
 * Represents the filters that can appear in url used to API calls.
 *
 * @author cartagena
 */
public final class Filter implements Cloneable {

  private Integer results;

  private Integer page;

  private Double priceMin;

  private Double priceMax;

  private Sort sort;

  private EBitMedal medal;

  public Filter() {}

  public Filter(Filter base) {
    this.results = base.results;
    this.page = base.page;
    this.priceMin = base.priceMin;
    this.priceMax = base.priceMax;
    this.sort = base.sort;
    this.medal = base.medal;
  }

  /**
   * @return the results
   */
  public final Integer getResults() {
    return this.results;
  }

  /**
   * @param results the results to set
   */
  public final void setResults(Integer results) {
    this.results = results;
  }

  /**
   * @return the page
   */
  public final Integer getPage() {
    return this.page;
  }

  /**
   * @param page the page to set
   */
  public final void setPage(Integer page) {
    this.page = page;
  }

  /**
   * @return the priceMin
   */
  public final Double getPriceMin() {
    return this.priceMin;
  }

  /**
   * @param priceMin the priceMin to set
   */
  public final void setPriceMin(Double priceMin) {
    this.priceMin = priceMin;
  }

  /**
   * @return the priceMax
   */
  public final Double getPriceMax() {
    return this.priceMax;
  }

  /**
   * @param priceMax the priceMax to set
   */
  public final void setPriceMax(Double priceMax) {
    this.priceMax = priceMax;
  }

  /**
   * @return the sort
   */
  public final Sort getSort() {
    return this.sort;
  }

  /**
   * @param sort the sort to set
   */
  public final void setSort(Sort sort) {
    this.sort = sort;
  }

  /**
   * @return the medal
   */
  public final EBitMedal getMedal() {
    return this.medal;
  }

  /**
   * @param medal the medal to set
   */
  public final void setMedal(EBitMedal medal) {
    this.medal = medal;
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

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#clone()
   */
  @Override
  public Filter clone() {
    return new Filter(this);
  }

}
