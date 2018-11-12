package org.whispersystems.circleservice.internal.push;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DeviceCode {

  @JsonProperty
  private String verificationCode;

  public String getVerificationCode() {
    return verificationCode;
  }
}
