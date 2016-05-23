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

import com.buscape.developer.result.type.Offer;

import org.brunocvcunha.taskerbox.core.ITaskerboxEmailable;
import org.brunocvcunha.taskerbox.core.TaskerboxChannel;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * Buscape Offer Wrapper - Emailable
 *
 * @author Bruno Candido Volpato da Cunha
 *
 */
@RequiredArgsConstructor
public class OfferWrapper implements ITaskerboxEmailable {

  @Getter
  @Setter
  private Offer value;

  public OfferWrapper(Offer value) {
    this.value = value;
  }

  @Override
  public String getEmailTitle(TaskerboxChannel<?> channel) {
    return this.value.getSeller().getSellerName() + ": " + this.value.getOfferName() + " - "
        + this.value.getPrice().getValue();
  }

  @Override
  public String getEmailBody(TaskerboxChannel<?> channel) {
    return this.value.getLinks().getLinks().get(0).getUrl();
  }
}
