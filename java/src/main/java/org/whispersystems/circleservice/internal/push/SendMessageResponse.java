package org.whispersystems.circleservice.internal.push;

public class SendMessageResponse {

  private boolean needsSync;

  public SendMessageResponse() {}

  public SendMessageResponse(boolean needsSync) {
    this.needsSync = needsSync;
  }

  public boolean getNeedsSync() {
    return needsSync;
  }
}
