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
package org.brunocvcunha.taskerbox.impl.buscape;

import com.buscape.developer.Buscape;
import com.buscape.developer.BuscapeException;
import com.buscape.developer.request.Filter;
import com.buscape.developer.result.type.Offer;
import com.buscape.developer.result.type.Result;
import com.sun.syndication.io.FeedException;

import java.io.IOException;

import org.brunocvcunha.taskerbox.core.TaskerboxChannel;
import org.brunocvcunha.taskerbox.core.annotation.TaskerboxField;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

/**
 * Buscape Channel
 *
 * @author Bruno Candido Volpato da Cunha
 *
 */
@Log4j
public class BuscapeChannel extends TaskerboxChannel<OfferWrapper> {

  @Getter
  @Setter
  @TaskerboxField("Desired Value")
  private double desiredValue;

  @Getter
  @Setter
  @TaskerboxField("App ID")
  private String appId;

  @Getter
  @Setter
  @TaskerboxField("Product")
  private int productId;

  @Getter
  @Setter
  @TaskerboxField("Found Value")
  private double foundValue;

  @Override
  public void setup() throws IOException {}

  @Override
  protected void execute() throws IOException, IllegalArgumentException, FeedException,
      BuscapeException {

    Filter filter = new Filter();
    Buscape buscape = new Buscape(this.appId, filter);

    Result maquina = buscape.offerListByProduct(this.productId);

    for (Offer offer : maquina.getOffers()) {
      if (isIgnoredSeller(offer.getSeller().getSellerName())
          || isIgnoredProduct(offer.getOfferName())) {
        continue;
      }

      double value = Double.valueOf(offer.getPrice().getValue());

      if (value < this.foundValue) {
        this.foundValue = value;
      }

      if (value < this.desiredValue) {
        logInfo(log, "[+] " + offer.getSeller().getSellerName() + " - " + offer.getOfferName()
            + " - " + value);
        performUnique(new OfferWrapper(offer));
      } else {
        logInfo(log, "[-] " + offer.getSeller().getSellerName() + " - " + offer.getOfferName()
            + " - " + value + " (Expected " + this.desiredValue + ")");
      }


    }
  }

  private boolean isIgnoredSeller(String sellerName) {
    return false;
  }

  private boolean isIgnoredProduct(String productName) {
    return false;
  }


  @Override
  protected String getItemFingerprint(OfferWrapper entry) {
    return entry.getValue().getSeller().getSellerName() + ":" + entry.getValue().getOfferName()
        + ":" + entry.getValue().getPrice().getValue();
  }

  @Override
  public String getDisplayName() {
    StringBuffer sb = new StringBuffer();
    sb.append(this.getId()).append(" (R$").append((int) this.getDesiredValue()).append(")");
    return sb.toString();
  }

}
