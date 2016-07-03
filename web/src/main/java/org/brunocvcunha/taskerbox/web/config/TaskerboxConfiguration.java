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
package org.brunocvcunha.taskerbox.web.config;

import io.dropwizard.Configuration;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Bruno 
 *
 */
public class TaskerboxConfiguration extends Configuration {

    @NotEmpty
    private String fileToUse;
    
    /**
     * @return the fileToUse
     */
    @JsonProperty
    public String getFileToUse() {
        return fileToUse;
    }

    /**
     * @param fileToUse the fileToUse to set
     */
    @JsonProperty
    public void setFileToUse(String fileToUse) {
        this.fileToUse = fileToUse;
    }

}
