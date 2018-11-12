package org.whispersystems.circleservice.internal.configuration;


import org.whispersystems.circleservice.api.push.TrustStore;

import okhttp3.ConnectionSpec;

public class SignalContactDiscoveryUrl extends SignalUrl {

  public SignalContactDiscoveryUrl(String url, TrustStore trustStore) {
    super(url, trustStore);
  }

  public SignalContactDiscoveryUrl(String url, String hostHeader, TrustStore trustStore, ConnectionSpec connectionSpec) {
    super(url, hostHeader, trustStore, connectionSpec);
  }
}
