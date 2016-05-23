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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import org.brunocvcunha.inutils4j.MyStringUtils;
import org.brunocvcunha.taskerbox.impl.jobs.vo.ScorerResult;
import org.brunocvcunha.taskerbox.impl.jobs.vo.TermScorer;

public class LinkedInJobDBComparer {

  private static final TermScorer[] TERMS = {new TermScorer("h1b", 10), new TermScorer("java", 10),
      new TermScorer("openedge", 20), new TermScorer("java ee", 10), new TermScorer("j2ee", 10),
      new TermScorer("ejb", 8), new TermScorer("flex", 10), new TermScorer("jsp", 10),
      new TermScorer("jsf", 2), new TermScorer("jpa", 8), new TermScorer("svn", 8),
      new TermScorer("git", 2), new TermScorer("spring", 5), new TermScorer("jax-ws", 8),
      new TermScorer("jax-rs", 8), new TermScorer("lucene", 8), new TermScorer("junit", 10),
      new TermScorer("jenkins", 10), new TermScorer("hudson", 10), new TermScorer("ant", 10),
      new TermScorer("maven", 10), new TermScorer("eclipse", 8), new TermScorer("xml", 5),
      new TermScorer("xslt", 5), new TermScorer("soap", 5), new TermScorer("rest", 5),
      new TermScorer("wsdl", 5), new TermScorer("netbeans", 5),

  };

  public static void main(String[] args) {

    File dir = new File("E:\\tmp\\job-db");

    for (File jobFile : dir.listFiles()) {
      if (jobFile.lastModified() < (System.currentTimeMillis() - ((60 * 1000 * 60 * 24) * 14))) {
        // System.out.println("IGNORE OLD " + new Date(jobFile.lastModified()));
        continue;
      }

      // System.out.println("Considering " + new Date(jobFile.lastModified()));
      if (jobFile.exists() && jobFile.isFile()) {

        try {
          String content = MyStringUtils.getContent(jobFile).toLowerCase();

          ScorerResult result = getScore(content);

          if (result.getScore() >= 5000) {

            // if (LinkedInJobSeeker.considerVisaDescription(content)
            // && LinkedInJobSeeker.considerExperienceDescription(content)) {
            System.out.println(jobFile.getName().split("\\.")[0] + " - Score: " + result.getScore()
                + " - " + result.getMatches());
            // }
          }
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }


  public static ScorerResult getScore(String content) {
    content = content.toLowerCase();

    int score = 0;
    List<String> foundTerms = new ArrayList<>();

    for (TermScorer scorer : TERMS) {
      if (content.contains(scorer.getTermLc())) {
        Matcher m = scorer.getPattern().matcher(content);
        if (m.find()) {
          foundTerms.add(scorer.getTerm());
          score += scorer.getScore() * 100;
        }
      }
    }

    return new ScorerResult(score, foundTerms);

  }
}
