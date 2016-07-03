Taskerbox
========

[![Apache License](http://img.shields.io/badge/license-ASL-blue.svg)](https://github.com/brunocvcunha/taskerbox/blob/master/LICENSE)
[![Build Status](https://travis-ci.org/brunocvcunha/taskerbox.svg)](https://travis-ci.org/brunocvcunha/taskerbox)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.brunocvcunha.taskerbox/taskerbox/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.brunocvcunha.taskerbox/taskerbox)
[![Coverage Status](https://coveralls.io/repos/github/brunocvcunha/taskerbox/badge.svg?branch=master)](https://coveralls.io/github/brunocvcunha/taskerbox?branch=master)

Java automation engine based on producers (channels) &amp; consumers (actions)

The main goal is to automate useful tasks. You can control running channels using Desktop UI or lightweight web panel.

Channels
--------
- Files
- Message Queues
- Price Finder
- Social Media
- Crawlers
- Job Seeker
- Server (Socket) Watchers
- Package Tracking

Actions
--------
- Desktop Toaster
- Email Sending
- SMS Sending ([Plivo](https://www.plivo.com/))
- Slack messaging


Example
--------

For example, to receive Tweet Messages in your desktop (Toaster Message), polling every minute:
```xml
<taskerbox>

  <org.brunocvcunha.taskerbox.impl.twitter.TwitterChannel
    id="TwitterToaster" every="60000" consumerKey="(consumerKey)"
    consumerSecret="(consumerSecret)"
    accessToken="(accessToken)"
    accessTokenSecret="(accessTokenSecret)">

    <org.brunocvcunha.taskerbox.impl.twitter.TwitterToasterAction />

  </org.brunocvcunha.taskerbox.impl.twitter.TwitterChannel>

</taskerbox>

```

To receive [Hacker News (YCombinator)](https://news.ycombinator.com/) in your Gmail, also popping up a Toaster (polling every two minutes):
```xml

<org.brunocvcunha.taskerbox.impl.feed.FeedChannel
  id="YCombinatorFeed" feedUrl="https://news.ycombinator.com/rss" every="120000">

  <org.brunocvcunha.taskerbox.impl.email.EmailAction
    smtpFrom="Taskerbox v0.1 &lt;taskerbox@brunocandido.com&gt;"
    smtpHost="mail.brunocandido.com" smtpPort="587" smtpUser="taskerbox@brunocandido.com"
    smtpPassword="(password)"  enableTLS="true" email="brunocvcunha@gmail.com" />

  <org.brunocvcunha.taskerbox.impl.feed.FeedToasterAction />

</org.brunocvcunha.taskerbox.impl.feed.FeedChannel>

```

To test if a specific URL returns an expected response, and send a message on Slack if it fails:

```xml
	<org.brunocvcunha.taskerbox.impl.http.HTTPUptimeChannel
		id="production" url="https://production-url/api/v1/status"
		contains="false" filter="expected-content-in-response" every="300000" numTries="2">

			<org.brunocvcunha.taskerbox.impl.slack.SlackAction
				token="xoxb-xxxxxxxxxxx-xxxxxxxxxxxxxxxxxxxxxxxx" iconEmoji=":see_no_evil:" slackChannel="#production"
				username="production-status" messageOverride="Production Server is down. Please check https://production-url/ "/>

	</org.brunocvcunha.taskerbox.impl.http.HTTPUptimeChannel>

```

Download
--------

Download [the latest JAR][1] or grab via Maven:
```xml
<dependency>
  <groupId>org.brunocvcunha.taskerbox</groupId>
  <artifactId>taskerbox-core</artifactId>
  <version>0.1</version>
</dependency>
```
or Gradle:
```groovy
compile 'org.brunocvcunha.taskerbox:taskerbox-core:0.1'
```

Snapshots of the development version are available in [Sonatype's `snapshots` repository][snap].

Taskerbox requires at minimum Java 7.


 [1]: https://search.maven.org/remote_content?g=org.brunocvcunha.taskerbox&a=taskerbox&v=LATEST
 [snap]: https://oss.sonatype.org/content/repositories/snapshots/
