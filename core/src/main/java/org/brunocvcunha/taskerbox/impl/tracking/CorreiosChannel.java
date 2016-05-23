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
package org.brunocvcunha.taskerbox.impl.tracking;

import java.util.List;

import org.alfredlibrary.AlfredException;
import org.alfredlibrary.utilitarios.correios.Rastreamento;
import org.alfredlibrary.utilitarios.correios.RegistroRastreamento;
import org.brunocvcunha.taskerbox.core.TaskerboxChannel;
import org.brunocvcunha.taskerbox.core.utils.validation.CorreiosTracking;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

/**
 * Corrreios Tracking Input Channel
 *
 * @author Bruno Candido Volpato da Cunha
 *
 */
@Log4j
public class CorreiosChannel extends TaskerboxChannel<CorreiosTrackingWrapper> {

  @CorreiosTracking
  @Getter
  @Setter
  private String tracking;

  @Getter
  @Setter
  private String descricao;

  @Override
  protected void execute() {
    logInfo(log, "Checking " + this.getId() + " - " + this.getDescricao() + " [" + this.tracking + "]");

    try {
      List<RegistroRastreamento> registros = Rastreamento.rastrear(this.tracking);

      for (int r = 0; r < registros.size(); r++) {
        RegistroRastreamento registro = registros.get(r);
        if (r == 0) {
          logInfo(log, "Status: " + registro.getDataHora() + " - " + registro.getAcao() + " - "
              + registro.getLocal() + " - " + registro.getDetalhe());
        }

        performUnique(new CorreiosTrackingWrapper(registro));


      }
    } catch (AlfredException e) {
      logWarn(log, "Error caught: " + e.getMessage());
    }

  }

  @Override
public String getItemFingerprint(CorreiosTrackingWrapper registro) {
    return this.tracking + ";" + registro.getValue().getDataHora() + ";" + registro.getValue().getAcao()
        + ";" + registro.getValue().getLocal() + ";" + registro.getValue().getDetalhe();
  }

  @Override
  public String getDisplayName() {
    return this.tracking + " (" + this.descricao + ")";
  }

  public static String formatTracking(RegistroRastreamento entry, String tracking, String descricao) {
    StringBuffer sb = new StringBuffer();
    sb.append(tracking).append(" - ").append(entry.getDataHora());

    if (descricao != null) {
      sb.append(" - ").append(descricao);
    }
    if (entry.getAcao() != null) {
      sb.append(" - ").append(entry.getAcao());
    }
    if (entry.getDetalhe() != null) {
      sb.append(" - ").append(entry.getDetalhe());
    }
    if (entry.getLocal() != null) {
      sb.append(" - ").append(entry.getLocal());
    }

    return sb.toString();
  }
}
