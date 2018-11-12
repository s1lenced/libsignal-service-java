package org.whispersystems.circleservice.api.websocket;


public interface ConnectivityListener {
  void onConnected();
  void onConnecting();
  void onDisconnected();
  void onAuthenticationFailure();
}
