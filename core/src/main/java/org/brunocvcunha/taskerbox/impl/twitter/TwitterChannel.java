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
package org.brunocvcunha.taskerbox.impl.twitter;

import java.util.List;

import org.brunocvcunha.taskerbox.core.TaskerboxChannel;
import org.brunocvcunha.taskerbox.core.annotation.TaskerboxField;
import org.hibernate.validator.constraints.NotEmpty;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Twitter Input Channel
 * 
 * @author Bruno Candido Volpato da Cunha
 * 
 */
@Log4j
public class TwitterChannel extends TaskerboxChannel<StatusWrapper> {

  private String loggedUser;

  @TaskerboxField("Username")
  @Getter
  @Setter
  private String username = "";

  @TaskerboxField("Filter")
  @Getter
  @Setter
  private String filter;

  @NotEmpty
  @TaskerboxField("Key")
  @Getter
  @Setter
  private String consumerKey;

  @NotEmpty
  @TaskerboxField("Secret")
  @Getter
  @Setter
  private String consumerSecret;

  @NotEmpty
  @TaskerboxField("Access Token")
  @Getter
  @Setter
  private String accessToken;

  @NotEmpty
  @TaskerboxField("Access Token Secret")
  @Getter
  @Setter
  private String accessTokenSecret;

  private Twitter twitter;


  @TaskerboxField("ignoreUsers")
  @Getter
  @Setter
  private List<String> ignoreUsers;

  @TaskerboxField("ignoreStarts")
  @Getter
  @Setter
  private List<String> ignoreStarts;

  @Override
  public void setup() throws IllegalStateException, TwitterException {
    logInfo(log, "Twitter setup...");

    ConfigurationBuilder cb = new ConfigurationBuilder();
    cb.setDebugEnabled(true).setOAuthConsumerKey(consumerKey)
        .setOAuthConsumerSecret(consumerSecret).setOAuthAccessToken(accessToken)
        .setOAuthAccessTokenSecret(accessTokenSecret);
    TwitterFactory tf = new TwitterFactory(cb.build());

    this.twitter = tf.getInstance();

    this.loggedUser = this.twitter.getScreenName();

    logInfo(log, "Twitter setup finished! Logged user is " + this.loggedUser);
  }

  @Override
  protected void execute() throws TwitterException {
    if (this.loggedUser == null) {
      setup();
    }

    if (username != null) {
      logInfo(log, "Checking tweets of @" + username + " with @" + loggedUser);
    } else {
      logInfo(log, "Checking tweets with @" + loggedUser);
    }

    ResponseList<Status> statusList;


    if (username == null || username.equals("")) {
      statusList = twitter.getHomeTimeline();
    } else {
      statusList = twitter.getUserTimeline(username);
    }

    status: for (Status status : statusList) {

      if (filter != null && !status.getText().contains(filter)) {
        continue;
      }


      StatusWrapper statusWrapper = new StatusWrapper(status);

      if (!alreadyPerformedAction(statusWrapper)) {
        if (ignoreUsers != null && ignoreUsers.contains(status.getUser().getScreenName())) {
          continue;
        }
        if (ignoreStarts != null) {
          for (String start : ignoreStarts) {
            if (status.getText().startsWith(start)) {
              continue status;
            }
          }
        }

        log.debug("Performing actions to entry: @" + status.getUser().getScreenName() + ": "
            + status.getText());

        perform(statusWrapper);
        addAlreadyPerformedAction(statusWrapper);
      }

    }
  }

  public String getItemFingerprint(StatusWrapper status) {
    return String.valueOf(status.getValue().getId());
  }


}
