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
package org.brunocvcunha.taskerbox.impl.jobs.vo;

import java.util.regex.Pattern;

public class TermScorer {

  private String term;

  private String termLc;

  private int score;

  private Pattern pattern;

  public TermScorer(String term, int score) {
    super();
    this.term = term;
    this.score = score;

    this.pattern = Pattern.compile("(?i).*?\\b" + term + "\\b.*?");
    this.termLc = term.toLowerCase();
  }

  public String getTerm() {
    return this.term;
  }

  public void setTerm(String term) {
    this.term = term;
  }

  public int getScore() {
    return this.score;
  }

  public void setScore(int score) {
    this.score = score;
  }

  public Pattern getPattern() {
    return this.pattern;
  }

  public void setPattern(Pattern pattern) {
    this.pattern = pattern;
  }

  public String getTermLc() {
    return this.termLc;
  }

  public void setTermLc(String termLc) {
    this.termLc = termLc;
  }



}
