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
package org.brunocvcunha.taskerbox.impl.custom;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.brunocvcunha.inutils4j.MyStringUtils;
import org.brunocvcunha.taskerbox.core.DefaultTaskerboxAction;
import org.jsoup.nodes.Document;

import lombok.extern.log4j.Log4j;

@Log4j
public class MeuIPAction extends DefaultTaskerboxAction<Document> {

  private String id;

  private File outputFile;

  @Override
  public void action(final Document entry) {

    String body = entry.html();
    // System.out.println(body);
    String ip =
        MyStringUtils.regexFindFirst("getElementById\\(\"div_reverso\"\\).innerHTML = \"(.*?)\"",
            body);
    logInfo(log, "IP Address Found: " + ip);

    if (this.outputFile != null) {
      FileWriter out;
      try {
        out = new FileWriter(this.outputFile, true);
        out.write(getTimestamp() + " " + InetAddress.getLocalHost().getHostName() + " - " + ip
            + "\r\n");
        out.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  @Override
public String getId() {
    return this.id;
  }

  @Override
public void setId(String id) {
    this.id = id;
  }

  public static String getTimestamp() {
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    return "[".concat(sdf.format(new Date())).concat("]");
  }

  public File getOutputFile() {
    return this.outputFile;
  }

  public void setOutputFile(File outputFile) {
    this.outputFile = outputFile;
  }



}
