/**
 * Copyright (C) 2014-2016 Open Whisper Systems
 *
 * Licensed according to the LICENSE file in this repository.
 */

package org.whispersystems.circleservice.api.messages.multidevice;

public class ReadMessage {

  private final String sender;
  private final long   timestamp;

  public ReadMessage(String sender, long timestamp) {
    this.sender    = sender;
    this.timestamp = timestamp;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public String getSender() {
    return sender;
  }

}
