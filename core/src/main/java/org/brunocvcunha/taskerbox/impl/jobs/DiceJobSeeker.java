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

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URLEncoder;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.brunocvcunha.taskerbox.core.http.TaskerboxHttpBox;
import org.brunocvcunha.taskerbox.impl.http.URLOpenerAction;
import org.brunocvcunha.taskerbox.impl.jobs.vo.ScorerResult;
import org.codehaus.jettison.json.JSONException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import lombok.Getter;
import lombok.Setter;
import lombok.val;
import lombok.extern.log4j.Log4j;

@Log4j
public class DiceJobSeeker extends DefaultJobSearchChannel {

  @Getter
  @Setter
  private String search;

  @Getter
  @Setter
  private boolean externalApply;

  public static void main(String[] args) throws Exception {

    DiceJobSeeker seeker = new DiceJobSeeker();
    seeker.setSearch("java");
    seeker.setup();
    seeker.setAction(new URLOpenerAction());
    seeker.execute();

  }

  public void bootstrapHttpClient(boolean fetchCookie) throws IOException {
    this.httpClient = TaskerboxHttpBox.getInstance().getHttpClient();
  }

  @Override
public void setup() {
    super.setup();

    try {
      bootstrapHttpClient(true);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  private boolean handleJob(String jobTitle, String jobEmployer, String location, String jobUrl)
      throws JSONException, ClientProtocolException, IOException, URISyntaxException {

    if (alreadyPerformedAction(jobUrl)) {
      return true;
    }

    String headline = jobUrl + " - " + location + " - " + jobTitle + " - " + jobEmployer;

    System.out.println(headline);

    if (!considerTitle(jobTitle)) {
      logInfo(log, "-- Ignored [title] " + headline);
      addAlreadyPerformedAction(jobUrl);
      return true;
    }


    if (!considerEmployer(jobEmployer)) {
      logInfo(log, "-- Ignored [employer] " + headline);
      addAlreadyPerformedAction(jobUrl);
      return true;
    }

    if (!considerLocation(location)) {
      logInfo(log, "-- Ignored [location] " + headline);
      addAlreadyPerformedAction(jobUrl);
      return true;
    }

    HttpEntity jobEntity = TaskerboxHttpBox.getInstance().getEntityForURL(jobUrl);
    String jobResult = TaskerboxHttpBox.getInstance().readResponseFromEntity(jobEntity);
    Document jobDocument = Jsoup.parse(jobResult);

    Elements elDescription = jobDocument.select("div.job_description");
    if (elDescription.isEmpty()) {
      elDescription = jobDocument.select("div#detailDescription");
    }

    /*
     * if (!jobDocument.html().contains("ApplyOnlineUrl: ''") &&
     * !jobDocument.html().contains("ApplyOnlineUrl: 'http://my.monster.com") && !externalApply) {
     * logInfo(log, "-- Ignored [externalApply] " + headline); addAlreadyPerformedAction(jobUrl);
     * return true; }
     */

    if (!considerVisaDescription(elDescription.html())) {
      logInfo(log, "-- Ignored [visa] " + headline);
      addAlreadyPerformedAction(jobUrl);
      return true;
    }
    if (!considerExperienceDescription(elDescription.html())) {
      log.info("-- Ignored [exp] " + headline);
      addAlreadyPerformedAction(jobUrl);
      return true;
    }

    ScorerResult result = LinkedInJobDBComparer.getScore(elDescription.html());

    if (result.getScore() < this.requiredScore) {
      logInfo(log, "-- Ignored [scorer] " + result.getScore() + " - " + result.getMatches() + " - "
          + headline);
      addAlreadyPerformedAction(jobUrl);
      return true;
    }

    headline = headline + " - " + result.getMatches();

    logInfo(log, "Open --> " + headline);
    // logInfo(log, elDescription.html());

    performUnique(jobUrl);

    try {
      Thread.sleep(5000L);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    return true;

  }

  @Override
  protected void execute() throws Exception {
    try {
      for (int x = 1; x < this.maxPages; x++) {
        int uniqueCount = 0;

        // DefaultHttpClient client =
        // TaskerboxHttpBox.getInstance().buildNewHttpClient();
        String seekUrl =
            "http://www.dice.com/job/results?n=50&q=" + URLEncoder.encode(this.search) + "&o="
                + (x * 50);
        logInfo(log, "... Seeking " + seekUrl);
        HttpEntity entity = TaskerboxHttpBox.getInstance().getEntityForURL(seekUrl);
        String result = TaskerboxHttpBox.getInstance().readResponseFromEntity(entity);

        if (result.contains("Sorry, no jobs were found that match your criteria")) {
          System.err.println("Busca encerrada.");
          this.bootstrapHttpClient(true);
          break;
          // return;
        }

        try {
          Document doc = Jsoup.parse(result);

          Elements el = doc.select("div#SRcolContainer").select("tr");

          for (val item : el) {
            Elements jobEls = item.select("a");
            if (jobEls.size() < 3) {
              continue;
            }

            String url = jobEls.get(0).attr("href");
            if (url.equals("")) {
              continue;
            }
            url = "http://www.dice.com" + url;

            if (url.contains("?src=")) {
              url = url.substring(0, url.indexOf("?src="));
            }

            String jobTitle = jobEls.get(0).text();
            String company = jobEls.get(1).text();
            String location = jobEls.get(2).text();

            // System.out.println("===============");
            // System.out.println(item.html());
            // System.out.println("===============");


            if (!jobTitle.equalsIgnoreCase("Job Title")) {
              handleJob(jobTitle, company, location, url);
              uniqueCount++;
            }

          }

          if (uniqueCount == 0) {
            logInfo(log, "DICE BREAK -- NO UNIQUE COUNT");
            break;
          }

          try {
            Thread.sleep(10000L);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }



}
