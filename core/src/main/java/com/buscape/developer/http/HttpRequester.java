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
package com.buscape.developer.http;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.brunocvcunha.taskerbox.core.http.TaskerboxHttpBox;

public final class HttpRequester {

  private URI uri;


  /**
   * Constructs HttpRequester object.
   *
   * @param uri a uri used in requests of the new object.
   */
  public HttpRequester(String url) {
    super();
    try {
      this.uri = new URI(url);
    } catch (URISyntaxException e) {
      throw new IllegalArgumentException(e);
    }
  }

  /**
   * Performs an http request in uri and returns its response.
   *
   * @return an string with the content of response.
   * @throws IOException if an I/O error occurs while reading the response.
   * @throws URISyntaxException
   * @throws IllegalStateException
   */
  public String getResponse() throws IOException, IllegalStateException, URISyntaxException {
    return readInputStream();
  }

  private String readInputStream() throws IOException, IllegalStateException, URISyntaxException {
    return TaskerboxHttpBox.getInstance().getStringBodyForURL(this.uri);
  }

}
