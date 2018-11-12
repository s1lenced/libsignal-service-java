/**
 * Copyright (C) 2014-2016 Open Whisper Systems
 *
 * Licensed according to the LICENSE file in this repository.
 */

package org.whispersystems.circleservice.internal.push.exceptions;

import org.whispersystems.circleservice.api.push.exceptions.NonSuccessfulResponseCodeException;
import org.whispersystems.circleservice.internal.push.MismatchedDevices;

public class MismatchedDevicesException extends NonSuccessfulResponseCodeException {

  private final MismatchedDevices mismatchedDevices;

  public MismatchedDevicesException(MismatchedDevices mismatchedDevices) {
    this.mismatchedDevices = mismatchedDevices;
  }

  public MismatchedDevices getMismatchedDevices() {
    return mismatchedDevices;
  }
}
