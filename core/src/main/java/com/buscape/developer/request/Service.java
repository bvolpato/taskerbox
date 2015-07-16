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

import com.buscape.developer.util.Messages;

public enum Service {

  LIST_CATEGORY(Messages.getString("Service.find-category-service")), //$NON-NLS-1$
  LIST_PRODUCT(Messages.getString("Service.find-product-service")), //$NON-NLS-1$
  LIST_OFFER(Messages.getString("Service.find-offer-service")), //$NON-NLS-1$
  TOP_PRODUCTS(Messages.getString("Service.top-products-service")), //$NON-NLS-1$
  USER_RATING(Messages.getString("Service.user-rating-service")), //$NON-NLS-1$
  DETAILS_PRODUCT(Messages.getString("Service.product-details-service")), //$NON-NLS-1$
  DETAILS_SELLER(Messages.getString("Service.seller-details-service")); //$NON-NLS-1$

  private String service;

  private Service(String service) {
    this.service = service;
  }

  @Override
  public String toString() {
    return this.service;
  }

}
