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
package com.buscape.developer.request.util;

import com.buscape.developer.request.Country;
import com.buscape.developer.request.EBitMedal;
import com.buscape.developer.request.Filter;
import com.buscape.developer.request.Parameters;
import com.buscape.developer.request.Service;
import com.buscape.developer.request.Sort;
import com.buscape.developer.result.ResultFormat;
import com.buscape.developer.util.Messages;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;


public final class URLBuilder {

  private String hostName;
  private Service service;
  private String applicationId;
  private Country countryCode;
  private ResultFormat format;
  private Filter filter;
  private Parameters parameters;

  public URLBuilder() {
    this.hostName = Messages.getString("URLBuilder.main-url"); //$NON-NLS-1$
    this.filter = new Filter();
    this.parameters = new Parameters();
    this.format = ResultFormat.XML;
  }

  public URLBuilder service(Service service) {
    this.service = service;
    return this;
  }

  public URLBuilder asCategoryListService() {
    this.service = Service.LIST_CATEGORY;
    return this;
  }

  public URLBuilder asProductListService() {
    this.service = Service.LIST_PRODUCT;
    return this;
  }

  public URLBuilder asOfferListService() {
    this.service = Service.LIST_OFFER;
    return this;
  }

  public URLBuilder asTopProductsService() {
    this.service = Service.TOP_PRODUCTS;
    return this;
  }

  public URLBuilder asUserRatingService() {
    this.service = Service.USER_RATING;
    return this;
  }

  public URLBuilder asProductDetailsService() {
    this.service = Service.DETAILS_PRODUCT;
    return this;
  }

  public URLBuilder asSellerDetailsService() {
    this.service = Service.DETAILS_SELLER;
    return this;
  }

  public URLBuilder applicationId(String applicationId) {
    this.applicationId = applicationId;
    return this;
  }

  public URLBuilder countryCode(String countryCode) {
    this.countryCode = Country.valueOf(countryCode);
    return this;
  }

  public URLBuilder countryCode(Country countryCode) {
    this.countryCode = countryCode;
    return this;
  }

  public URLBuilder filter(Filter filter) {
    this.filter = filter;
    return this;
  }

  public URLBuilder parameters(Parameters parameters) {
    this.parameters = parameters;
    return this;
  }

  public URLBuilder categoryIdParam(String categoryId) {
    this.parameters.setCategoryId(Integer.parseInt(categoryId));
    return this;
  }

  public URLBuilder categoryIdParam(int categoryId) {
    this.parameters.setCategoryId(categoryId);
    return this;
  }

  public URLBuilder productIdParam(String productId) {
    this.parameters.setProductId(Integer.parseInt(productId));
    return this;
  }

  public URLBuilder productIdParam(int productId) {
    this.parameters.setProductId(productId);
    return this;
  }

  public URLBuilder sellerIdParam(String sellerId) {
    this.parameters.setSellerId(Integer.parseInt(sellerId));
    return this;
  }

  public URLBuilder sellerIdParam(int sellerId) {
    this.parameters.setSellerId(sellerId);
    return this;
  }

  public URLBuilder keywordParam(String keyword) {
    this.parameters.setKeyword(keyword);
    return this;
  }

  public URLBuilder barcodeParam(String barcode) {
    this.parameters.setBarcode(barcode);
    return this;
  }

  public URLBuilder formatFilter(String format) {
    this.format = ResultFormat.fromString(format);
    return this;
  }

  public URLBuilder formatFilter(ResultFormat format) {
    this.format = format;
    return this;
  }

  public URLBuilder resultsFilter(String results) {
    this.filter.setResults(Integer.parseInt(results));
    return this;
  }

  public URLBuilder resultsFilter(int results) {
    this.filter.setResults(results);
    return this;
  }

  public URLBuilder pageFilter(String page) {
    this.filter.setPage(Integer.parseInt(page));
    return this;
  }

  public URLBuilder pageFilter(int page) {
    this.filter.setPage(page);
    return this;
  }

  public URLBuilder priceMinFilter(String priceMin) {
    this.filter.setPriceMin(Double.parseDouble(priceMin));
    return this;
  }

  public URLBuilder priceMinFilter(double priceMin) {
    this.filter.setPriceMin(priceMin);
    return this;
  }

  public URLBuilder priceMaxFilter(String priceMax) {
    this.filter.setPriceMax(Double.parseDouble(priceMax));
    return this;
  }

  public URLBuilder priceMaxFilter(double priceMax) {
    this.filter.setPriceMax(priceMax);
    return this;
  }

  public URLBuilder sortFilter(String sort) {
    this.filter.setSort(Sort.valueOf(sort));
    return this;
  }

  public URLBuilder sortFilter(Sort sort) {
    this.filter.setSort(sort);
    return this;
  }

  public URLBuilder medalFilter(String medal) {
    this.filter.setMedal(EBitMedal.valueOf(medal));
    return this;
  }

  public URLBuilder medalFilter(EBitMedal medal) {
    this.filter.setMedal(medal);
    return this;
  }

  public String build() {
    StringBuilder sb = new StringBuilder();

    sb.append(this.hostName);
    sb.append("/"); //$NON-NLS-1$
    sb.append(this.service);
    sb.append("/"); //$NON-NLS-1$
    sb.append(this.applicationId);
    sb.append("/"); //$NON-NLS-1$
    sb.append(this.countryCode.code());
    sb.append("/?"); //$NON-NLS-1$
    sb.append(formatFiltersAndParameters());

    return sb.toString();
  }

  private String formatFiltersAndParameters() {
    Map<String, Object> map = new HashMap<>();
    map.putAll(this.filter.asMap());
    map.putAll(this.parameters.asMap());
    map.put(Messages.getString("URLBuilder.format"), this.format.toString()); //$NON-NLS-1$

    StringBuilder sb = new StringBuilder();

    if (!map.isEmpty()) {
      for (Entry<String, Object> entry : map.entrySet()) {
        sb.append("&" + entry.getKey() + "=" + entry.getValue()); //$NON-NLS-1$ //$NON-NLS-2$
      }
    }

    return sb.substring(1).toString();
  }

}
