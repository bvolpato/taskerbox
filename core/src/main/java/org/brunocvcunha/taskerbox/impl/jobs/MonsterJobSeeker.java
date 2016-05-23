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
import org.brunocvcunha.taskerbox.core.annotation.TaskerboxField;
import org.brunocvcunha.taskerbox.core.http.TaskerboxHttpBox;
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
public class MonsterJobSeeker extends DefaultJobSearchChannel {

  @Getter
  @Setter
  @TaskerboxField("Search")
  private String search;

  @Getter
  @Setter
  @TaskerboxField("External Apply")
  private boolean externalApply;

  @Getter
  @Setter
  @TaskerboxField("Site")
  private String site = "com";

  public static void main(String[] args) throws Exception {

    MonsterJobSeeker seeker = new MonsterJobSeeker();
    seeker.setSearch("h1b");
    seeker.setup();
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

    try {
      Thread.sleep(1000L);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    HttpEntity jobEntity = TaskerboxHttpBox.getInstance().getEntityForURL(jobUrl);
    String jobResult = TaskerboxHttpBox.getInstance().readResponseFromEntity(jobEntity);
    Document jobDocument = Jsoup.parse(jobResult);

    Elements elDescription = jobDocument.select("div#jobBodyContent");

    if (!jobDocument.html().contains("ApplyOnlineUrl: ''")
        && !jobDocument.html().contains("ApplyOnlineUrl: 'http://my.monster.com") && !this.externalApply) {
      logInfo(log, "-- Ignored [externalApply] " + headline);
      addAlreadyPerformedAction(jobUrl);
      return true;
    }

    if (!considerVisaDescription(elDescription.html())) {
      logInfo(log, "-- Ignored [visa] " + headline);
      addAlreadyPerformedAction(jobUrl);
      return true;
    }
    if (!considerExperienceDescription(elDescription.html())) {
      logInfo(log, "-- Ignored [exp] " + headline);
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
            "http://jobsearch.monster." + this.site + "/search/?q=" + URLEncoder.encode(this.search)
                + "&sort=dt.rv.di&pg=" + x;
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

          Elements el = doc.select("table.listingsTable").select("tr");

          for (val item : el) {
            Elements jobTitleEl = item.select("div.jobTitleContainer");
            Elements companyEl = item.select("div.companyContainer");
            Elements locationEl = item.select("div.jobLocationSingleLine");

            // aaa
            String url = jobTitleEl.select("a").attr("href");
            if (url.equals("")) {
              continue;
            }

            if (url.contains("?mescoid")) {
              url = url.substring(0, url.indexOf("?mescoid"));
            }
            if (url.contains("?jobPosition")) {
              url = url.substring(0, url.indexOf("?jobPosition"));
            }
            if (url.contains("&jobPosition")) {
              url = url.substring(0, url.indexOf("&jobPosition"));
            }

            String company = "";
            if (!companyEl.select("a").isEmpty()) {
              company = companyEl.select("a").get(0).attr("title");
            }

            handleJob(jobTitleEl.text(), company, locationEl.select("a").text(), url);

            uniqueCount++;
          }

          if (uniqueCount == 0) {
            logInfo(log, "MONSTER BREAK -- NO UNIQUE COUNT");
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
