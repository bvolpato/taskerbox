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
package org.brunocvcunha.taskerbox.impl.jobs;

import java.awt.Desktop;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.brunocvcunha.inutils4j.MyStringUtils;

public class LinkedInJobApplyFileReader {
  public static void main(String[] args) throws IOException, URISyntaxException,
      InterruptedException {
    List<String> toApply =
        MyStringUtils.getContentLines(new File(
            "C:\\Users\\bruno.cunha\\Dropbox\\Bruno\\Taskerbox\\TOAPPLY.txt"));

    for (String string : toApply) {
      String url = string.split(";")[0];
      System.out.println(url);

      if (!GraphicsEnvironment.isHeadless()) {
        Desktop.getDesktop().browse(new URI(url));
      }

      Thread.sleep(500L);

    }
  }
}
