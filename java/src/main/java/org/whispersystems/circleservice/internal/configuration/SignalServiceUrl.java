package org.whispersystems.circleservice.internal.configuration;


import org.whispersystems.circleservice.api.push.TrustStore;

import okhttp3.ConnectionSpec;

public class SignalServiceUrl extends SignalUrl {

  public SignalServiceUrl(String url, TrustStore trustStore) {
    super(url, trustStore);
  }

  public SignalServiceUrl(String url, String hostHeader, TrustStore trustStore, ConnectionSpec connectionSpec) {
    super(url, hostHeader, trustStore, connectionSpec);
  }
}
