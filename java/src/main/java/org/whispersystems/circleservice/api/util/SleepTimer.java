package org.whispersystems.circleservice.api.util;

public interface SleepTimer {
  public void sleep(long millis) throws InterruptedException;
}
