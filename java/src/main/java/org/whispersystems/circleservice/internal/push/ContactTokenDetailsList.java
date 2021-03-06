/**
 * Copyright (C) 2014-2016 Open Whisper Systems
 *
 * Licensed according to the LICENSE file in this repository.
 */

package org.whispersystems.circleservice.internal.push;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.whispersystems.circleservice.api.push.ContactTokenDetails;

import java.util.List;

public class ContactTokenDetailsList {

  @JsonProperty
  private List<ContactTokenDetails> contacts;

  public ContactTokenDetailsList() {}

  public List<ContactTokenDetails> getContacts() {
    return contacts;
  }
}
