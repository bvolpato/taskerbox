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
		username="production-status" messageOverride="Production Server is down. Please check https://production-url/"/>

</org.brunocvcunha.taskerbox.impl.http.HTTPUptimeChannel>

```

Usage
--------

Create a XML configuration file with the desired channels and actions.

Example:
```xml
<taskerbox>
		
	<org.brunocvcunha.taskerbox.impl.http.HTTPUptimeChannel
		id="production" url="https://production-url/api/v1/status"
		contains="false" filter="expected-content-in-response" every="300000" numTries="2">
	
		<org.brunocvcunha.taskerbox.impl.slack.SlackAction
			token="xoxb-xxxxxxxxxxx-xxxxxxxxxxxxxxxxxxxxxxxx" iconEmoji=":see_no_evil:" slackChannel="#production"
			username="production-status" messageOverride="Production Server is down. Please check https://production-url/"/>
	
	</org.brunocvcunha.taskerbox.impl.http.HTTPUptimeChannel>


</taskerbox>
```

Create a Yaml file with the server configuration, that points the `fileToUse` to the file created above.

Example:
```
server:
  applicationConnectors:
  - type: http 
    port: 8000
  adminConnectors:
  - type: http
    port: 8001

fileToUse: /home/bruno/taskerbox.xml
```




Download the [release JAR](https://github.com/brunocvcunha/taskerbox/releases), and start using `java -jar taskerbox.jar server /path/to/taskerbox.yml`.

It will start a HTTP server in the port specified to access: [http://localhost:8000/static/index.html](http://localhost:8000/static/index.html), with all the jobs configured in the XML file.

![Sample Screenshot](https://github.com/brunocvcunha/taskerbox/blob/master/.meta/ss.png?raw=true)



Taskerbox requires at minimum Java 7.

