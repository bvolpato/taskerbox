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
package org.brunocvcunha.taskerbox.impl.sockettester;

import org.brunocvcunha.sockettester.core.SocketTesterController;
import org.brunocvcunha.sockettester.vo.SocketTesterVO;
import org.brunocvcunha.taskerbox.core.TaskerboxChannel;
import org.brunocvcunha.taskerbox.core.annotation.TaskerboxField;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

@Log4j
public class SocketTesterChannel extends TaskerboxChannel<SocketTesterVO> {

  @TaskerboxField("Type")
  @Getter
  @Setter
  private String type;

  @TaskerboxField("Name")
  @Getter
  @Setter
  private String name;

  @TaskerboxField("Host")
  @Getter
  @Setter
  private String host;

  @TaskerboxField("Port")
  @Getter
  @Setter
  private int port;

  @TaskerboxField("Service")
  @Getter
  @Setter
  private String service;

  @Getter
  @Setter
  private String status;

  @Override
  public void setup() {}

  @Override
  protected void execute() throws Exception {

    SocketTesterVO vo = new SocketTesterVO();
    vo.setName(this.name);
    vo.setHost(this.host);
    vo.setPort(this.port);
    vo.setService(this.service);
    vo.setStatus(this.status);
    vo.setType(this.type);

    log.debug("Validating service " + this.id + " - " + vo);
    SocketTesterController.validate(vo);

    if (!vo.isValid()) {
      perform(vo);
    }
  }

  @Override
  protected String getItemFingerprint(SocketTesterVO entry) {
    return entry.toString();
  }

  @Override
  public String getDisplayName() {
    StringBuffer sb = new StringBuffer();
    sb.append(this.getId());
    if (this.getName() != null && !this.getName().equals("")) {
      sb.append(" (").append(this.getName()).append(")");
    }
    return sb.toString();
  }

  @Override
  public String toString() {
    return "SocketTesterChannel [type=" + this.type + ", name=" + this.name + ", host=" + this.host + ", port="
        + this.port + ", status=" + this.status + ", service=" + this.service + "]";
  }

}
