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
package org.brunocvcunha.taskerbox.impl.airfare;

import com.sun.syndication.io.FeedException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.Min;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.brunocvcunha.taskerbox.core.TaskerboxChannel;
import org.brunocvcunha.taskerbox.core.annotation.TaskerboxField;
import org.brunocvcunha.taskerbox.core.http.TaskerboxHttpBox;
import org.brunocvcunha.taskerbox.impl.email.EmailValueVO;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.log4j.Log4j;

/**
 * Fluig Input Channel
 *
 * @author Bruno Candido Volpato da Cunha
 *
 */
@Log4j
@ToString(includeFieldNames = true)
public class MatrixITAChannel extends TaskerboxChannel<EmailValueVO> {

  @TaskerboxField("Cookie")
  @Getter
  @Setter
  private String cookie;

  @TaskerboxField("From")
  @Getter
  @Setter
  private String from;

  @TaskerboxField("To")
  @Getter
  @Setter
  private String to;

  @TaskerboxField("Start Date")
  @Getter
  @Setter
  private String startDate;

  @TaskerboxField("End Date")
  @Getter
  @Setter
  private String endDate;

  @TaskerboxField("Desired Value")
  @Getter
  @Setter
  private double desiredValue;

  @TaskerboxField("Days Min")
  @Getter
  @Setter
  @Min(1)
  private int daysMin = 13;

  @TaskerboxField("Days Max")
  @Getter
  @Setter
  @Min(1)
  private int daysMax = 17;

  @TaskerboxField(value = "Min Price Found", readOnly = true)
  @Getter
  @Setter
  private double priceFound;

  @TaskerboxField(value = "Min Price Date", readOnly = true)
  @Getter
  @Setter
  private String minPriceDate;

  @Getter
  @Setter
  private DefaultHttpClient client;

  @Override
  public void setup() throws IOException, URISyntaxException {
    log.debug("Setup at MatrixITAChannel...");

    this.client = TaskerboxHttpBox.getInstance().buildNewHttpClient();

    CookieStore store = this.client.getCookieStore();
    store.addCookie(TaskerboxHttpBox.buildCookie("PREF", this.cookie, "matrix.itasoftware.com", "/"));

    TaskerboxHttpBox.getInstance().getResponseForURL(this.client, "http://matrix.itasoftware.com/");
  }

  @Override
  protected void execute() throws IOException, IllegalArgumentException, FeedException {

    try {
      HttpPost post = new HttpPost("http://matrix.itasoftware.com/xhr/shop/search");
      post.addHeader("Accept", "*/*");
      post.addHeader("X-Requested-With", "XMLHttpRequest");

      List<NameValuePair> pairs = new ArrayList<>();
      pairs.add(new BasicNameValuePair("name", "calendar"));
      pairs.add(new BasicNameValuePair("summarizers",
          "calendar,overnightFlightsCalendar,itineraryStopCountList,itineraryCarrierList"));
      pairs.add(new BasicNameValuePair("format", "JSON"));

      JSONObject inputs = new JSONObject();
      inputs.put("slices", new JSONArray("[{\"origins\":[\"" + this.from
          + "\"],\"originPreferCity\":false,\"destinations\":[\"" + this.to
          + "\"],\"destinationPreferCity\":false},{\"destinations\":[\"" + this.from
          + "\"],\"destinationPreferCity\":false,\"origins\":[\"" + this.to
          + "\"],\"originPreferCity\":false}]"));
      inputs.put("startDate", this.startDate);
      inputs.put("layover", new JSONObject("{\"max\":" + this.daysMax + ",\"min\":" + this.daysMin + "}"));
      inputs.put("pax", new JSONObject("{\"adults\":1}"));
      inputs.put("cabin", "COACH");
      inputs.put("changeOfAirport", true);
      inputs.put("checkAvailability", true);
      inputs.put("firstDayOfWeek", "SUNDAY");
      inputs.put("endDate", this.endDate);

      // System.out.println(inputs.toString());
      pairs.add(new BasicNameValuePair("inputs", inputs.toString()));

      UrlEncodedFormEntity entity = new UrlEncodedFormEntity(pairs);
      post.setEntity(entity);

      log.debug("Executing request on " + post.getURI() + "...");
      HttpResponse response = this.client.execute(post);

      int statusCode = response.getStatusLine().getStatusCode();
      if (statusCode != HttpStatus.SC_OK) {
        logWarn(log, "Error while fetching " + response.getStatusLine());
        return;
      }

      String responseBody =
          TaskerboxHttpBox.getInstance().readResponseFromEntity(response.getEntity());

      log.debug("Got Response: " + responseBody);
      if (!responseBody.startsWith("{}&&")) {
        throw new IllegalArgumentException("Invalid response");
      }

      if (responseBody.contains("QPX capacity exceeded")
          || responseBody.contains("Internal server error")
          || responseBody.contains("Query timeout")) {
        Thread.sleep(10000L);

        setup();
        execute();
        return;
      }

      String validJson = responseBody.substring(4);

      JSONObject obj = new JSONObject(validJson);

      try {

        boolean hasExpectedPrice = false;
        double cheapest = 0;
        String cheapestStr = "";
        String cheapestDate = "";

        Map<String, String> valueMap = new LinkedHashMap<>();

        JSONObject result = obj.getJSONObject("result");
        JSONObject calendar = result.getJSONObject("calendar");
        JSONObject itineraryCarrierList = result.getJSONObject("itineraryCarrierList");

        JSONArray months = calendar.getJSONArray("months");

        for (int month = 0; month < months.length(); month++) {
          JSONObject monthObject = months.getJSONObject(month);
          // System.out.println(monthObject.getInt("month") + "/" +
          // monthObject.getInt("year"));

          JSONArray weeks = monthObject.getJSONArray("weeks");
          for (int week = 0; week < weeks.length(); week++) {

            JSONObject weekObject = weeks.getJSONObject(week);
            JSONArray days = weekObject.getJSONArray("days");
            for (int day = 0; day < days.length(); day++) {
              JSONObject dayObject = days.getJSONObject(day);

              if (dayObject.getInt("solutionCount") > 0) {

                // System.out.println(dayObject);
                if (dayObject.has("minPriceInCalendar")
                    && dayObject.getBoolean("minPriceInCalendar")) {

                  String date =
                      dayObject.getInt("date") + "/" + monthObject.getInt("month") + "/"
                          + monthObject.getInt("year");


                  String price = dayObject.getString("minPrice");
                  double value = 0;

                  if (price.contains("BRL")) {
                    value = Double.valueOf(price.replace("BRL", ""));
                  }

                  if (cheapest == 0 || value < cheapest) {
                    cheapest = value;
                    cheapestStr = price;
                    cheapestDate = date;
                  }

                  if (value <= this.desiredValue) {

                    hasExpectedPrice = true;

                    valueMap.put(date, price);

                    logInfo(log, "Found expected price! " + price + " (" + value
                        + ") - Desired value: " + this.desiredValue);


                  }
                }
              }

            }

          }

        }


        this.priceFound = cheapest;
        this.minPriceDate = cheapestDate;

        if (hasExpectedPrice) {

          EmailValueVO email = new EmailValueVO();
          email.setTitle("Taskerbox - " + this.from + " x " + this.to + " - Found: " + cheapestStr);

          StringBuffer sb = new StringBuffer();

          sb.append(email.getTitle());
          sb.append("<br><br>");

          for (String date : valueMap.keySet()) {
            sb.append(date).append(": ").append(valueMap.get(date)).append("<br>");
          }

          sb.append("<br>");

          JSONArray carriers = itineraryCarrierList.getJSONArray("groups");
          for (int carrier = 0; carrier < carriers.length(); carrier++) {
            JSONObject carrierObject = carriers.getJSONObject(carrier);
            JSONObject valueObject = carrierObject.getJSONObject("label");
            sb.append("Companhia: ");
            if (carrierObject.has("minPriceInSummary")) {
              sb.append("*");
            }
            sb.append(valueObject.getString("shortName"));
            sb.append(" - ");

            sb.append(carrierObject.getString("minPrice"));
            sb.append("<br>");
          }
          sb.append("<br>");
          sb.append("At http://matrix.itasoftware.com/");

          email.setBody(sb.toString());

          // perform(email);
          performUnique(email);

        } else {
          logInfo(log, "Value " + cheapestStr + " is not desired value (" + this.desiredValue
              + ") - Cheapest Date: " + cheapestDate);
        }

      } catch (Exception e) {
        File save = File.createTempFile("matrixita", ".txt");

        logError(log, "Error occurred with " + this.toString() + " - " + e.getMessage()
            + " - Saving to: " + save.getAbsolutePath());

        FileWriter out = new FileWriter(save);
        out.write(responseBody);
        out.close();
        // log.debug("responseBody: " + responseBody);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  @Override
  protected String getItemFingerprint(EmailValueVO entry) {
    String considerateBody = entry.getBody();
    if (considerateBody.contains("Companhia")) {
      considerateBody = considerateBody.substring(0, considerateBody.indexOf("Companhia"));
    }
    return entry.getTitle() + " - " + considerateBody;
  }

  @Override
  public String getDisplayName() {
    StringBuffer sb = new StringBuffer();
    sb.append(this.getId()).append(" (R$").append((int) this.getDesiredValue()).append(")");
    return sb.toString();
  }


}
