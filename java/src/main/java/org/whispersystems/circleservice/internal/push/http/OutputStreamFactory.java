package org.whispersystems.circleservice.internal.push.http;


import org.whispersystems.circleservice.api.crypto.DigestingOutputStream;

import java.io.IOException;
import java.io.OutputStream;

public interface OutputStreamFactory {

  public DigestingOutputStream createFor(OutputStream wrap) throws IOException;

}
