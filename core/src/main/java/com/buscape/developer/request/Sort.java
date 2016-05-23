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

/**
 * Types of sorting available in API calls.
 *
 * @author neto
 */
public enum Sort {

  /**
   * Descending ordering by price of parcel.
   */
  D_INSTALLMENT(Messages.getString("Sort.desc-installment")), //$NON-NLS-1$

  /**
   * Descending ordering by number of parcels.
   */
  D_NUMBEROFINSTALLMENTS(Messages.getString("Sort.desc-num-installment")), //$NON-NLS-1$

  /**
   * Descending ordering by price.
   */
  D_PRICE(Messages.getString("Sort.desc-price")), //$NON-NLS-1$

  /**
   * Descending ordering by user rating.
   */
  D_RATE(Messages.getString("Sort.desc-rate")), //$NON-NLS-1$

  /**
   * Descending ordering by seller.
   */
  D_SELLER(Messages.getString("Sort.desc-seller")), //$NON-NLS-1$

  /**
   * Ascending ordering by price of parcel.
   */
  INSTALLMENT(Messages.getString("Sort.installment")), //$NON-NLS-1$

  /**
   * Ascending ordering by number of parcel.
   */
  NUMBEROFINSTALLMENTS(Messages.getString("Sort.num-installment")), //$NON-NLS-1$

  /**
   * Ascending ordering by price.
   */
  PRICE(Messages.getString("Sort.price")), //$NON-NLS-1$

  /**
   * Ascending ordering by user rating.
   */
  RATE(Messages.getString("Sort.rate")), //$NON-NLS-1$

  /**
   * Ascending ordering by seller.
   */
  SELLER(Messages.getString("Sort.seller")), //$NON-NLS-1$

  /**
   * Ascending ordering by seal of trusted store.
   */
  TRUSTEDSTORE(Messages.getString("Sort.trusted-store")); //$NON-NLS-1$


  private String sort;

  private Sort(String sort) {
    this.sort = sort;
  }

  /**
   * @return string representation of sorting type
   */
  @Override
  public String toString() {
    return this.sort;
  }
}
