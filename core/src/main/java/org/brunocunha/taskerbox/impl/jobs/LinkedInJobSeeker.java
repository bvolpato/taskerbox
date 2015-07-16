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
package org.brunocunha.taskerbox.impl.jobs;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.brunocunha.taskerbox.core.annotation.TaskerboxField;
import org.brunocunha.taskerbox.core.http.TaskerboxHttpBox;
import org.brunocunha.taskerbox.impl.jobs.vo.ScorerResult;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

@Log4j
public class LinkedInJobSeeker extends DefaultJobSearchChannel {

  @Getter
  @Setter
  private List<Long> openIds = new ArrayList<Long>();

  @Getter
  @Setter
  @TaskerboxField("Search")
  private String search;

  @Getter
  @Setter
  @TaskerboxField("Countries")
  private String[] countries;

  @Getter
  @Setter
  private File toApplyFile;

  @Getter
  @Setter
  @TaskerboxField("External Apply")
  private boolean externalApply;

  @Getter
  @Setter
  private String tempDir = "e:\\tmp";

  @Getter
  @Setter
  @TaskerboxField("User Email")
  private String userEmail;

  @Getter
  @Setter
  @TaskerboxField("User Password")
  private String userPassword;

  @Getter
  @Setter
  @TaskerboxField("Date Facet")
  // 1=1 day ago, 2=2-7 days, 3=8-14 days, 4=15-30 days
  private String dateFacet = "1,2,3";

  public void bootstrapLinkedInHttpClient(boolean fetchCookie) throws ClientProtocolException,
      IllegalStateException, IOException, URISyntaxException {
    this.httpClient = TaskerboxHttpBox.getInstance().getHttpClient();

    HttpGet get = new HttpGet("https://www.linkedin.com/");
    HttpResponse getResponse = this.httpClient.execute(get);

    String getContent = EntityUtils.toString(getResponse.getEntity());

    Document getDoc = Jsoup.parse(getContent);

    String loginCsrfParam = getDoc.select("input[name=loginCsrfParam]").attr("value");
    String csrfToken = getDoc.select("input[name=csrfToken]").attr("value");

    logInfo(log, loginCsrfParam);

    HttpPost post = new HttpPost("https://www.linkedin.com/uas/login-submit");
    List<NameValuePair> pairs2 = new ArrayList<NameValuePair>();
    pairs2.add(new BasicNameValuePair("isJsEnabled", "true"));
    pairs2.add(new BasicNameValuePair("source_app", ""));
    pairs2.add(new BasicNameValuePair("session_key", userEmail));
    pairs2.add(new BasicNameValuePair("session_password", userPassword));
    pairs2.add(new BasicNameValuePair("session_redirect", ""));
    pairs2.add(new BasicNameValuePair("trk", ""));
    pairs2.add(new BasicNameValuePair("loginCsrfParam", loginCsrfParam));
    pairs2.add(new BasicNameValuePair("fromEmail", ""));
    pairs2.add(new BasicNameValuePair("csrfToken", csrfToken));
    pairs2.add(new BasicNameValuePair("sourceAlias",
        "0_7r5yezRXCiA_H0CRD8sf6DhOjTKUNps5xGTqeX8EEoi"));
    pairs2.add(new BasicNameValuePair("client_ts", "1413507675390"));
    pairs2.add(new BasicNameValuePair("client_r", "a@gmail.com:812661382:422199706:736472965"));
    pairs2.add(new BasicNameValuePair("client_output", "-1850142"));
    pairs2.add(new BasicNameValuePair("client_n", "812661382:422199706:736472965"));
    pairs2.add(new BasicNameValuePair("client_v", "1.0.1"));


    UrlEncodedFormEntity entity2 = new UrlEncodedFormEntity(pairs2);
    post.setEntity(entity2);

    this.httpClient.execute(post);


  }

  public void setup() {
    super.setup();

    try {
      bootstrapLinkedInHttpClient(true);
    } catch (ClientProtocolException e) {
      e.printStackTrace();
    } catch (IllegalStateException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (URISyntaxException e) {
      e.printStackTrace();
    }
  }


  private boolean handleJob(JSONObject job) throws JSONException, ClientProtocolException,
      IOException, URISyntaxException {
    if (job.getBoolean("isApplied")) {
      return false;
    }

    long jobId = job.getLong("id");

    if (!openIds.contains(jobId)) {
      openIds.add(jobId);
      // uniqueCount++;
    } else {
      return false;
    }

    String jobTitle = job.getString("fmt_jobTitle").replaceAll("</?B>", "");

    if (!externalApply && job.has("sourceDomain")) {
      logInfo(log, jobId + " - " + jobTitle + " - " + job.getString("sourceDomain")
          + " --> ignored [external]");
      return true;
    }



    String jobEmployer = job.getString("fmt_companyName");

    String jobUrl = "https://www.linkedin.com/jobs2/view/" + jobId;
    if (alreadyPerformedAction(jobUrl)) {
      return true;
    }

    String location = "";
    if (job.has("fmt_location")) {
      location = job.getString("fmt_location");
    }
    String headline = jobUrl + " - " + location + " - " + jobTitle + " - " + jobEmployer;

    if (job.has("sourceDomain")) {
      String sourceDomain = job.getString("sourceDomain");
      if (externalApply
          && (sourceDomain.contains("empregocerto.uol.com.br")
              || sourceDomain.contains("jobomas.com") || sourceDomain.contains("curriculum.com.br"))) {
        logInfo(log, "-- Ignored [externalApply - domain " + sourceDomain + "] " + headline);
        addAlreadyPerformedAction(jobUrl);
        return true;
      }
    }

    if (!considerTitle(jobTitle)) {
      logInfo(log, "-- Ignored [title] " + headline);
      addAlreadyPerformedAction(jobUrl);
      return true;
    }

    try {
      FileWriter out = new FileWriter(new File(tempDir + "\\job-db\\_titles.txt"), true);
      out.write(jobTitle + "\r\n");
      out.close();
    } catch (Exception e) {
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
    Elements elDescription = jobDocument.select("div.description-section").select("div.rich-text");
    Elements elSkills = jobDocument.select("div.skills-section").select("div.rich-text");


    // FileWriter out = new FileWriter(new File(tempDir + "\\job-db\\" + jobId + ".txt"));
    // out.write(elDescription.text() + "\r\n");
    // out.write(elSkills.text());
    // out.close();

    if (!externalApply && !jobResult.contains("onsite-apply")) {
      logInfo(log, "-- Ignored [onsite apply] " + headline);
      addAlreadyPerformedAction(jobUrl);

      try {
        Thread.sleep(5000L);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

      return true;
    }

    if (!considerVisaDescription(elDescription.html()) || !considerVisaDescription(elSkills.html())) {
      logInfo(log, "-- Ignored [visa] " + headline);
      addAlreadyPerformedAction(jobUrl);
      return true;
    }
    if (!considerExperienceDescription(elDescription.html())
        || !considerExperienceDescription(elSkills.html())) {
      logInfo(log, "-- Ignored [exp] " + headline);
      addAlreadyPerformedAction(jobUrl);
      return true;
    }

    ScorerResult result =
        LinkedInJobDBComparer.getScore(elDescription.html() + " - " + elSkills.html());

    if (result.getScore() < requiredScore) {
      logInfo(log, "-- Ignored [scorer] " + result.getScore() + " - " + result.getMatches() + " - "
          + headline);
      addAlreadyPerformedAction(jobUrl);
      return true;
    }

    headline = headline + " - " + result.getMatches();

    logInfo(log, headline);
    logInfo(log, elDescription.html());

    performUnique(jobUrl);

    try {
      Thread.sleep(5000L);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    return true;

  }

  public BasicClientCookie buildCookie(String name, String value) {
    return TaskerboxHttpBox.buildCookie(name, value, "www.linkedin.com", "/");
  }

  @Override
  protected void execute() throws Exception {

    for (String country : countries) {
      try {
        int strikeCount = 0;

        for (int x = 1; x < maxPages; x++) {

          // If channel is paused, stop execution
          if (this.isPaused() && !this.isForced()) {
            return;
          }

          int uniqueCount = 0;

          // DefaultHttpClient client =
          // TaskerboxHttpBox.getInstance().buildNewHttpClient();
          String seekUrl =
              "https://www.linkedin.com/vsearch/jj?keywords=" + URLEncoder.encode(search)
                  + "&countryCode=" + country
                  + "&sortBy=DD&orig=JSHP&distance=100&locationType=I&openFacets=L,C,N&page_num="
                  + x + "&pt=jobs&f_TP=" + dateFacet;
          logInfo(log, "... Seeking " + seekUrl);
          HttpEntity entity = TaskerboxHttpBox.getInstance().getEntityForURL(seekUrl);
          String result = TaskerboxHttpBox.getInstance().readResponseFromEntity(entity);
          if (result.contains("<title>Sign Up | LinkedIn</title>")
              || result.contains("<title>LinkedIn | LinkedIn</title>")
              || result.contains("<p class=\"signin-link\">Already have an account?")) {
            logError(log, "Solicitado login... Saindo.");

            this.bootstrapLinkedInHttpClient(true);
            continue;
            // return;
          }

          try {
            JSONArray jobs =
                new JSONObject(result).getJSONObject("content").getJSONObject("page")
                    .getJSONObject("voltron_unified_search_json").getJSONObject("search")
                    .getJSONArray("results");

            for (int j = 0; j < jobs.length(); j++) {
              try {
                JSONObject idxObject = jobs.getJSONObject(j);
                if (!idxObject.has("job")) {
                  continue;
                }

                JSONObject job = idxObject.getJSONObject("job");

                if (handleJob(job)) {
                  uniqueCount++;
                }

              } catch (Exception e) {
                logError(log, "Exception reading --> " + jobs.get(j));
                e.printStackTrace();
              }

            }

            if (uniqueCount == 0) {
              logInfo(log, "Zero unique count. Striking...");

              strikeCount++;
              if (strikeCount > 2) {
                logInfo(log, "BREAK -- ZERO UNIQUE COUNT! STRIKES!");
                break;
              }
            }

            try {
              Thread.sleep(10000L);
            } catch (InterruptedException e) {
              e.printStackTrace();
              return;
            }

            // If channel is paused, stop execution
            if (this.isPaused() && !this.isForced()) {
              logInfo(log, "Channel is paused, interrupting [2]...");
              return;
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


}
