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
package org.brunocvcunha.taskerbox.impl.irc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class IRCChannel {

  public static void main(String[] args) throws Exception {

    // The server to connect to and our details.
    String server = "irc.freenode.net";
    String nick = "simple_bot";
    String login = "simple_bot";

    // The channel which the bot will join.
    String channel = "#freenode";

    // Connect directly to the IRC server.
    Socket socket = new Socket(server, 6667);
    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

    // Log on to the server.
    writer.write("NICK " + nick + "\r\n");
    writer.write("USER " + login + " 8 * : Java IRC Hacks Bot\r\n");
    writer.flush();

    // Read lines from the server until it tells us we have connected.
    String line = null;
    while ((line = reader.readLine()) != null) {
      if (line.indexOf("004") >= 0) {
        // We are now logged in.
        break;
      } else if (line.indexOf("433") >= 0) {
        System.out.println("Nickname is already in use.");
        return;
      }
    }

    // Join the channel.
    writer.write("JOIN " + channel + "\r\n");
    writer.flush();

    // Keep reading lines from the server.
    while ((line = reader.readLine()) != null) {
      if (line.toLowerCase().startsWith("PING ")) {
        // We must respond to PINGs to avoid being disconnected.
        writer.write("PONG " + line.substring(5) + "\r\n");
        writer.write("PRIVMSG " + channel + " :I got pinged!\r\n");
        writer.flush();
      } else {
        // Print the raw line received by the bot.
        System.out.println(line);
      }
    }


    socket.close();
  }

}
