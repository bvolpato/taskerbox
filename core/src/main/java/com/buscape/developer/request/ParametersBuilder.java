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

public final class ParametersBuilder {

  private final Parameters params;

  public ParametersBuilder() {
    this.params = new Parameters();
  }

  public ParametersBuilder categoryId(Integer categoryId) {
    this.params.setCategoryId(categoryId);
    return this;
  }

  public ParametersBuilder productId(Integer productId) {
    this.params.setProductId(productId);
    return this;
  }

  public ParametersBuilder sellerId(Integer sellerId) {
    this.params.setSellerId(sellerId);
    return this;
  }

  public ParametersBuilder keyword(String keyword) {
    this.params.setKeyword(keyword);
    return this;
  }

  public ParametersBuilder barcode(String barcode) {
    this.params.setBarcode(barcode);
    return this;
  }

  public Parameters build() {
    return this.params;
  }

}
