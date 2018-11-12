/*
 * Copyright (C) 2014-2016 Open Whisper Systems
 *
 * Licensed according to the LICENSE file in this repository.
 */

package org.whispersystems.circleservice.api;

import org.whispersystems.libsignal.InvalidMessageException;
import org.whispersystems.libsignal.util.guava.Optional;
import org.whispersystems.circleservice.api.crypto.AttachmentCipherInputStream;
import org.whispersystems.circleservice.api.crypto.ProfileCipherInputStream;
import org.whispersystems.circleservice.api.crypto.UnidentifiedAccess;
import org.whispersystems.circleservice.api.messages.SignalServiceAttachment.ProgressListener;
import org.whispersystems.circleservice.api.messages.SignalServiceAttachmentPointer;
import org.whispersystems.circleservice.api.messages.SignalServiceDataMessage;
import org.whispersystems.circleservice.api.messages.SignalServiceEnvelope;
import org.whispersystems.circleservice.api.profiles.SignalServiceProfile;
import org.whispersystems.circleservice.api.push.SignalServiceAddress;
import org.whispersystems.circleservice.api.util.CredentialsProvider;
import org.whispersystems.circleservice.api.util.SleepTimer;
import org.whispersystems.circleservice.api.websocket.ConnectivityListener;
import org.whispersystems.circleservice.internal.configuration.SignalServiceConfiguration;
import org.whispersystems.circleservice.internal.push.PushServiceSocket;
import org.whispersystems.circleservice.internal.push.SignalServiceEnvelopeEntity;
import org.whispersystems.circleservice.internal.util.StaticCredentialsProvider;
import org.whispersystems.circleservice.internal.websocket.WebSocketConnection;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

/**
 * The primary interface for receiving Signal Service messages.
 *
 * @author Moxie Marlinspike
 */
@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class SignalServiceMessageReceiver {

  private final PushServiceSocket          socket;
  private final SignalServiceConfiguration urls;
  private final CredentialsProvider        credentialsProvider;
  private final String                     userAgent;
  private final ConnectivityListener       connectivityListener;
  private final SleepTimer                 sleepTimer;

  /**
   * Construct a SignalServiceMessageReceiver.
   *
   * @param urls The URL of the Signal Service.
   * @param user The Signal Service username (eg. phone number).
   * @param password The Signal Service user password.
   * @param signalingKey The 52 byte signaling key assigned to this user at registration.
   */
  public SignalServiceMessageReceiver(SignalServiceConfiguration urls,
                                      String user, String password,
                                      String signalingKey, String userAgent,
                                      ConnectivityListener listener,
                                      SleepTimer timer)
  {
    this(urls, new StaticCredentialsProvider(user, password, signalingKey), userAgent, listener, timer);
  }

  /**
   * Construct a SignalServiceMessageReceiver.
   *
   * @param urls The URL of the Signal Service.
   * @param credentials The Signal Service user's credentials.
   */
  public SignalServiceMessageReceiver(SignalServiceConfiguration urls,
                                      CredentialsProvider credentials,
                                      String userAgent,
                                      ConnectivityListener listener,
                                      SleepTimer timer)
  {
    this.urls                 = urls;
    this.credentialsProvider  = credentials;
    this.socket               = new PushServiceSocket(urls, credentials, userAgent);
    this.userAgent            = userAgent;
    this.connectivityListener = listener;
    this.sleepTimer           = timer;
  }

  /**
   * Retrieves a SignalServiceAttachment.
   *
   * @param pointer The {@link SignalServiceAttachmentPointer}
   *                received in a {@link SignalServiceDataMessage}.
   * @param destination The download destination for this attachment.
   *
   * @return An InputStream that streams the plaintext attachment contents.
   * @throws IOException
   * @throws InvalidMessageException
   */
  public InputStream retrieveAttachment(SignalServiceAttachmentPointer pointer, File destination, int maxSizeBytes)
      throws IOException, InvalidMessageException
  {
    return retrieveAttachment(pointer, destination, maxSizeBytes, null);
  }

  public SignalServiceProfile retrieveProfile(SignalServiceAddress address, Optional<UnidentifiedAccess> unidentifiedAccess)
    throws IOException
  {
    return socket.retrieveProfile(address, unidentifiedAccess);
  }

  public InputStream retrieveProfileAvatar(String path, File destination, byte[] profileKey, int maxSizeBytes)
    throws IOException
  {
    socket.retrieveProfileAvatar(path, destination, maxSizeBytes);
    return new ProfileCipherInputStream(new FileInputStream(destination), profileKey);
  }

  /**
   * Retrieves a SignalServiceAttachment.
   *
   * @param pointer The {@link SignalServiceAttachmentPointer}
   *                received in a {@link SignalServiceDataMessage}.
   * @param destination The download destination for this attachment.
   * @param listener An optional listener (may be null) to receive callbacks on download progress.
   *
   * @return An InputStream that streams the plaintext attachment contents.
   * @throws IOException
   * @throws InvalidMessageException
   */
  public InputStream retrieveAttachment(SignalServiceAttachmentPointer pointer, File destination, int maxSizeBytes, ProgressListener listener)
      throws IOException, InvalidMessageException
  {
    if (!pointer.getDigest().isPresent()) throw new InvalidMessageException("No attachment digest!");

    socket.retrieveAttachment(pointer.getId(), destination, maxSizeBytes, listener);
    return AttachmentCipherInputStream.createFor(destination, pointer.getSize().or(0), pointer.getKey(), pointer.getDigest().get());
  }

  /**
   * Creates a pipe for receiving SignalService messages.
   *
   * Callers must call {@link SignalServiceMessagePipe#shutdown()} when finished with the pipe.
   *
   * @return A SignalServiceMessagePipe for receiving Signal Service messages.
   */
  public SignalServiceMessagePipe createMessagePipe() {
    WebSocketConnection webSocket = new WebSocketConnection(urls.getSignalServiceUrls()[0].getUrl(),
                                                            urls.getSignalServiceUrls()[0].getTrustStore(),
                                                            Optional.of(credentialsProvider), userAgent, connectivityListener,
                                                            sleepTimer);

    return new SignalServiceMessagePipe(webSocket, Optional.of(credentialsProvider));
  }

  public SignalServiceMessagePipe createUnidentifiedMessagePipe() {
    WebSocketConnection webSocket = new WebSocketConnection(urls.getSignalServiceUrls()[0].getUrl(),
                                                            urls.getSignalServiceUrls()[0].getTrustStore(),
                                                            Optional.<CredentialsProvider>absent(), userAgent, connectivityListener,
                                                            sleepTimer);

    return new SignalServiceMessagePipe(webSocket, Optional.of(credentialsProvider));
  }

  public List<SignalServiceEnvelope> retrieveMessages() throws IOException {
    return retrieveMessages(new NullMessageReceivedCallback());
  }

  public List<SignalServiceEnvelope> retrieveMessages(MessageReceivedCallback callback)
      throws IOException
  {
    List<SignalServiceEnvelope>       results  = new LinkedList<>();
    List<SignalServiceEnvelopeEntity> entities = socket.getMessages();

    for (SignalServiceEnvelopeEntity entity : entities) {
      SignalServiceEnvelope envelope;

      if (entity.getSource() != null && entity.getSourceDevice() > 0) {
        envelope = new SignalServiceEnvelope(entity.getType(), entity.getSource(),
                                             entity.getSourceDevice(), entity.getTimestamp(),
                                             entity.getMessage(), entity.getContent(),
                                             entity.getServerTimestamp(), entity.getServerUuid());
      } else {
        envelope = new SignalServiceEnvelope(entity.getType(), entity.getTimestamp(),
                                             entity.getMessage(), entity.getContent(),
                                             entity.getServerTimestamp(), entity.getServerUuid());
      }

      callback.onMessage(envelope);
      results.add(envelope);

      if (envelope.hasUuid()) socket.acknowledgeMessage(envelope.getUuid());
      else                    socket.acknowledgeMessage(entity.getSource(), entity.getTimestamp());
    }

    return results;
  }


  public interface MessageReceivedCallback {
    public void onMessage(SignalServiceEnvelope envelope);
  }

  public static class NullMessageReceivedCallback implements MessageReceivedCallback {
    @Override
    public void onMessage(SignalServiceEnvelope envelope) {}
  }

}
