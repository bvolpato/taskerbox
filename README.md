Taskerbox
========

[![Apache License](http://img.shields.io/badge/license-ASL-blue.svg)](https://github.com/brunocvcunha/inutils4j/blob/master/LICENSE)

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
- WhatsApp


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
