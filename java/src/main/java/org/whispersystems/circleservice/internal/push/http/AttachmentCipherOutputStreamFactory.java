package org.whispersystems.circleservice.internal.push.http;


import org.whispersystems.circleservice.api.crypto.AttachmentCipherOutputStream;
import org.whispersystems.circleservice.api.crypto.DigestingOutputStream;

import java.io.IOException;
import java.io.OutputStream;

public class AttachmentCipherOutputStreamFactory implements OutputStreamFactory {

  private final byte[] key;

  public AttachmentCipherOutputStreamFactory(byte[] key) {
    this.key = key;
  }

  @Override
  public DigestingOutputStream createFor(OutputStream wrap) throws IOException {
    return new AttachmentCipherOutputStream(key, wrap);
  }

}
